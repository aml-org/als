package org.mulesoft.als.suggestions.antlr.suggestor

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.misc.IntervalSet
import org.mulesoft.als.suggestions.antlr.suggestor.Implicits.{MutListImpl, MutMapImpl}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


object Core {
  val followSetsByATN: mutable.Map[String, FollowSetsPerState] = new mutable.LinkedHashMap[String, FollowSetsPerState]()
}

class Core (parser: Parser, atn: ATN, vocabulary: Vocabulary) {


  // A mapping of rule index + token stream position to end token positions.
  // A rule which has been visited before with the same input position will always produce the same output positions.
  private val shortcutMap = new mutable.LinkedHashMap[Int, mutable.LinkedHashMap[Int, RuleEndStatus]]()

  // not used for now, put here in order to leave logic coded
  // Rules which replace any candidate token they contain.
  // This allows to return descriptive rules (e.g. className, instead of ID/identifier).
  private val preferredRules: Set[Int] = Set[Int]()

  // Specify if preferred rules should translated top-down (higher index rule returns first) or
  // bottom-up (lower index rule returns first).
  private val translateRulesTopDown = false

  // not used for now, put here in order to leave logic coded
  // Tailoring of the result:
  // Tokens which should not appear in the candidates set.
  private val ignoredTokens: Set[Int] = Set[Int]()

  // The collected candidates (rules and tokens).
  private val candidates: CandidatesCollection = new CandidatesCollection()

  private val precedenceStack = new mutable.MutableList[Int]()

  private var tokenStartIndex = 0
  private var statesProcessed: Int = 0

  private val tokens = new mutable.ListBuffer[Token]()

  /**
   * @param line: starts at line 1
   * @param column
   * @param curCaret
   * @return
   */
  def caretTokenForPosition(line: Int, column: Int, curCaret: Int = 0): Int =
    getCaretIfExists(curCaret) match {
      case Some(value) if value.getLine < line => caretTokenForPosition(line, column, curCaret + 1)
      case Some(value) if value.getLine == line && value.getStopIndex < column => caretTokenForPosition(line, column, curCaret + 1)
      case _ => curCaret
    }

  /** returns None if it exceeds token size */
  def getCaretIfExists(curCaret: Int): Option[Token] = {
    Option(try {
      parser.getTokenStream.get(curCaret)
    } catch {
      case _: IndexOutOfBoundsException => null
    })
  }

  def collectCandidates(caretTokenIndex: Int, ctx: Option[ParserRuleContext] = None): CandidatesCollection = {
    shortcutMap.clear()
    candidates.clear()
    tokens.clear()
    statesProcessed = 0
    precedenceStack.clear()
    tokenStartIndex = ctx.map(_.start.getStartIndex).getOrElse(0)
    traverseTokens(parser.getInputStream, tokenStartIndex, caretTokenIndex)

    val callStack: RuleWithStartTokenList = new ListBuffer[RuleWithStartToken]()
    val startRule = ctx.map(_.getRuleIndex).getOrElse(0)
    processRule(atn.ruleToStartState(startRule), 0, callStack, 0)
    candidates
  }

  private def traverseTokens(tokenStream: TokenStream, start: Int, finish: Int): Unit = {
    var offset: Int = start
    var end = false
    do {
      val token = tokenStream.get(offset)
      offset = offset + 1
      if (token.getChannel == Token.DEFAULT_CHANNEL) {
        tokens.push(token)
        if (token.getTokenIndex >= finish || endOfFile(token)) end = true
      }
      // Do not check for the token index here, as we want to end with the first unhidden token on or after
      // the caret.
      else if (endOfFile(token)) end = true
    } while (!end)
  }


  /**
   Walks the rule chain upwards or downwards (depending on translateRulesTopDown) to see if that matches any of the preferred rules. If found, that rule is added to the collection candidates and true is returned.
    Params:
    ruleWithStartTokenList – The list to convert.
    Returns:
    true if any of the stack entries was converted.
   */
  private def translateStackToRuleIndex(ruleWithStartTokenList: List[RuleWithStartToken]): Boolean =
    preferredRules.nonEmpty && innerTranslateToRuleIndex(ruleWithStartTokenList)

  private def innerTranslateToRuleIndex(ruleWithStartTokenList: List[RuleWithStartToken]): Boolean = {
    val ruleList = if(translateRulesTopDown) ruleWithStartTokenList.reverse else ruleWithStartTokenList
    ruleList.indices.exists(i => translateToRuleIndex(i, ruleList))
  }

  private def translateToRuleIndex(i: Int, ruleWithStartTokenList: List[RuleWithStartToken]): Boolean = {
    val rule = ruleWithStartTokenList(i)
    val ruleIndex = rule.ruleIndex
    val startTokenIndex = rule.startTokenIndex
    if(preferredRules.contains(ruleIndex)) {
      val path = ruleWithStartTokenList.slice(0, i).map(r => r.ruleIndex)

      @tailrec
      def addNew(idx: Int): Boolean =
        if(candidates.rules.size <= idx) false
        else {
          val rule = candidates.rules(idx)
          if(idx != ruleIndex || rule.ruleList.length != path.length) addNew(idx+1)
          else if (path.indices.forall{ i => path(i) == rule.ruleList(i)}) true
          else addNew(idx+1)
        }

      if(addNew(0))
        candidates.rules.put(ruleIndex, CandidateRule(startTokenIndex, path.toIndexedSeq))
      true
    } else false
  }

  def processRule(startState: RuleStartState, tokenListIndex: Int, callStack: RuleWithStartTokenList, indentation: Int): RuleEndStatus = {

  /**
      For rule start states we determine and cache the follow set, which gives us 3 advantages:
      1) We can quickly check if a symbol would be matched when we follow that rule. We can so check in advance
        and can save us all the intermediate steps if there is no match.
      2) We'll have all symbols that are collectable already together when we are at the caret on rule enter.
      3) We get this lookup for free with any 2nd or further visit of the same rule, which often happens
      in non trivial grammars, especially with (recursive) expressions and of course when invoking code
        completion multiple times.
     */
    def calculateResult(): RuleEndStatus = {
      val setsPerState: mutable.Map[Int, FollowSetsHolder] =
        Core.followSetsByATN.getOrInitialize(parser.getGrammarFileName, () => new mutable.LinkedHashMap[Int, FollowSetsHolder])

      val followSets = setsPerState.getOrInitialize(startState.stateNumber, seekFollowStates)

      val startTokenIndex = tokens(tokenListIndex).getTokenIndex
      callStack.push(new RuleWithStartToken(startTokenIndex, startState.ruleIndex))

      val result: mutable.Set[Int] = mutable.HashSet[Int]()

      if(tokenListIndex >= tokens.length -1) { // at caret?
         if (preferredRules.contains(startState.ruleIndex))
         // No need to go deeper when collecting entries and we reach a rule that we want to collect anyway.
              this.translateStackToRuleIndex(callStack.toList)
          else {
           // Convert all follow sets to either single symbols or their associated preferred rule and add
           // the result to our candidates list.
           followSets.sets.foreach { set =>
             val fullPath = callStack.clone()
             set.path.foreach(path => fullPath.push(new RuleWithStartToken(startTokenIndex, path)))
             if (!translateStackToRuleIndex(fullPath.toList)) {
               set.intervals.toArray.foreach{symbol =>
                 if(!ignoredTokens.contains(symbol)) {
                   if(!candidates.tokens.contains(symbol)) candidates.tokens.put(symbol, (getLiteralNameWithoutQuotation(symbol), set.following))
                   else if (candidates.tokens(symbol)._2 != set.following)
                     candidates.tokens.put(symbol, (getLiteralNameWithoutQuotation(symbol), IndexedSeq.empty))
                 }
               }
             }
           }
         }
        callStack.pop()
        result.toSet
      } else if (!followSets.combined.contains(Token.EPSILON) && !followSets.combined.contains(tokens(tokenListIndex).getType)) {
        // Process the rule if we either could pass it without consuming anything (epsilon transition)
        // or if the current input symbol will be matched somewhere after this entry point.
        // Otherwise stop here.
          callStack.pop()
          result.toSet
      } else {
        // The current state execution pipeline contains all yet-to-be-processed ATN states in this rule.
        // For each such state we store the token index + a list of rules that lead to it.
        val statePipeline: PipelineEntryList = ListBuffer.empty

        // Bootstrap the pipeline.
        statePipeline.push(PipelineEntry(startState, tokenListIndex));

        while(statePipeline.nonEmpty) {
          val currentEntry = statePipeline.pop()
          statesProcessed = statesProcessed + 1
          val currentSymbol = tokens(currentEntry.tokenListIndex).getType
          val atCaret = currentEntry.tokenListIndex >= tokens.length -1
          if(currentEntry.state.getStateType == ATNState.RULE_STOP)
            result.add(currentEntry.tokenListIndex)
          else {
            val transitions = currentEntry.state.getTransitions
            transitions.foreach {
              case transition: RuleTransition =>
                val endStatus: RuleEndStatus = processRule(transition.target.asInstanceOf[RuleStartState],
                  currentEntry.tokenListIndex,
                  callStack,
                  indentation + 1)
                endStatus.foreach(position => statePipeline.push(PipelineEntry(transition.followState, position)))
              case transition if transition.getSerializationType == Transition.PREDICATE =>
                if(checkPredicate(transition.asInstanceOf[PredicateTransition]))
                  statePipeline.push(PipelineEntry(transition.target, currentEntry.tokenListIndex))
              case transition: PrecedencePredicateTransition =>
                if(transition.precedence >= this.precedenceStack.last)
                  statePipeline.push(PipelineEntry(transition.target, currentEntry.tokenListIndex))
              case transition: WildcardTransition =>
                if(atCaret) {
                  if(!translateStackToRuleIndex(callStack.toList))
                    IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType).toArray
                      .foreach(symbol => if(!ignoredTokens.contains(symbol))
                        candidates.tokens.put(symbol, (getLiteralNameWithoutQuotation(symbol), IndexedSeq.empty))
                      )
                  else
                    statePipeline.push(PipelineEntry(transition.target, currentEntry.tokenListIndex + 1))
                }
              case transition: EpsilonTransition =>
                statePipeline.push(PipelineEntry(transition.target, currentEntry.tokenListIndex))
              case transition =>
                var set = Option(transition.label())
                if(set.isDefined && set.exists(_.size() > 0)) {
                  if(transition.getSerializationType == Transition.NOT_SET)
                    set = set.map(_.complement(IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType)))
                }
                if(atCaret){
                  if(!translateStackToRuleIndex(callStack.toList)){
                    val list: Array[Int] = set.map(_.toArray).getOrElse(Array.empty)
                    val addFollowing = list.length == 1
                    list.foreach{symbol =>
                      if(!ignoredTokens.contains(symbol)){
                        if(addFollowing) candidates.tokens.put(symbol, (getLiteralNameWithoutQuotation(symbol), getFollowingTokens(transition)))
                        else candidates.tokens.put(symbol, (getLiteralNameWithoutQuotation(symbol), IndexedSeq.empty))
                      }
                    }
                  }
                } else {
                  if(set.exists(_.contains(currentSymbol)))
                    statePipeline.push(PipelineEntry(transition.target, currentEntry.tokenListIndex + 1))
                }
            }
          }
        }
        callStack.pop()

        shortcutMap.get(startState.ruleIndex).foreach(_.put(tokenListIndex, result.toSet))
        result.toSet
      }
    }

    def seekFollowStates(): FollowSetsHolder = {
      val followSets = new FollowSetsHolder()
      val stop = atn.ruleToStopState(startState.ruleIndex)
      determineFollowSets(startState,stop, followSets.sets)

      // Sets are split by path to allow translating them to preferred rules. But for quick hit tests
      // it is also useful to have a set with all symbols combined.
      followSets.sets.foreach { set =>
        followSets.combined.addAll(set.intervals)
      }
      followSets
    }

    /**
     * Entry point for the recursive follow set collection function.
     *
     * @param start Start state.
     * @param stop Stop state.
     * @param sets The mutable list of sets to be determined.
     */
    def determineFollowSets(start: ATNState, stop: ATNState, sets: mutable.ListBuffer[FollowSetWithPath]): Unit = {
      sets.clear()
      val stateStack: mutable.ListBuffer[ATNState] = mutable.ListBuffer.empty
      val ruleStack: mutable.ListBuffer[Int] = mutable.ListBuffer.empty
      collectFollowSets(start, stop, sets, stateStack, ruleStack)
    }

    /**
     Collects possible tokens which could be matched following the given ATN state. This is essentially the same algorithm as used in the LL1Analyzer class, but here we consider predicates also and use no parser rule context.
      Params:
      s – The state to continue from.
      stopState – The state which ends the collection routine.
      followSets – A pass through parameter to add found sets to.
      stateStack – A stack to avoid endless recursions.
      ruleStack – The current rule stack.
    */
    def collectFollowSets(start: ATNState,
                          stop: ATNState,
                          followSets: mutable.ListBuffer[FollowSetWithPath],
                          stateStack: mutable.ListBuffer[ATNState],
                          ruleStack: mutable.ListBuffer[Int]): Unit = {
      if(stateStack.contains(start)) { /* end recursion */ }
      else {
        stateStack.push(start)
        if(start == stop || start.getStateType == ATNState.RULE_STOP) {
          val set = new FollowSetWithPath()
          set.intervals = IntervalSet.of(Token.EPSILON)
          set.path = ruleStack.toIndexedSeq
          followSets.push(set)
          stateStack.pop()
          /* end recursion */
        } else {
          start.getTransitions.foreach {
            case ruleTransition: RuleTransition if ruleStack.contains(ruleTransition.target.ruleIndex) => // do nothing
            case predicateTransition: PredicateTransition if !checkPredicate(predicateTransition) => // do nothing
            case ruleTransition: RuleTransition =>
              ruleStack.push(ruleTransition.target.ruleIndex)
              collectFollowSets(ruleTransition.target, stop, followSets, stateStack, ruleStack)
              ruleStack.pop()
            case predicateTransition: PredicateTransition =>
              collectFollowSets(predicateTransition.target, stop, followSets, stateStack, ruleStack)
            case transition if transition.isEpsilon =>
              collectFollowSets(transition.target, stop, followSets, stateStack, ruleStack)
            case transition if transition.getSerializationType == Transition.WILDCARD =>
              val set = new FollowSetWithPath()
              set.intervals = IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, this.atn.maxTokenType)
              set.path = ruleStack.toIndexedSeq
              followSets.push(set) // LA PAPA
            case transition =>
              Option(transition.label).foreach { label =>
                if(label.size() > 0) {
                  val myLabel = if(transition.getSerializationType == Transition.NOT_SET)
                    label.complement(IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType)) else label
                  val set = new FollowSetWithPath()
                  set.intervals = myLabel
                  set.path = ruleStack.toIndexedSeq
                  set.following = getFollowingTokens(transition)
                  followSets.push(set) // OTHER PAPA
                }
              }
          }
          stateStack.pop()
        }
      }
    }


    shortcutMap.getOrInitialize(startState.ruleIndex, () => new mutable.LinkedHashMap[Int, RuleEndStatus]())
      .getOrInitialize(tokenListIndex, calculateResult)
  }

  private def getLiteralNameWithoutQuotation(symbol: Int): Option[String] =
    Option(vocabulary.getLiteralName(symbol)).map(_.stripPrefix("'").stripSuffix("'"))

  /**
   *This method follows the given transition and collects all symbols within the same rule that directly follow it without intermediate transitions to other rules and only if there is a single symbol for a transition.
    *Params:
      *transition – The transition from which to start.
    *Returns:
      *A list of toke types.
   */
  private def getFollowingTokens(transition: Transition): TokenList = {
    val pipeline: mutable.ListBuffer[ATNState] = new ListBuffer[ATNState]()
    var result: TokenList = IndexedSeq.empty
    pipeline.push(transition.target)
    while(pipeline.nonEmpty) {
      val state = pipeline.pop()
      state.getTransitions.foreach{outgoing =>
        if(outgoing.getSerializationType == Transition.ATOM) {
          if(!outgoing.isEpsilon) {
            val list = outgoing.label().toArray
            if(list.length == 1 && !ignoredTokens.contains(list.head)) {
              result = result :+ list.head
              pipeline.push(outgoing.target)
            }
          } else {
            pipeline.push(outgoing.target)
          }
        }
      }
    }
    result
  }

  private def checkPredicate(transition: PredicateTransition): Boolean =
    transition.getPredicate.eval(parser, new ParserRuleContext())


  private def endOfFile(token: Token): Boolean = token.getType == Token.EOF
}


sealed class RuleWithStartToken(var startTokenIndex: Int, var ruleIndex: Int)
