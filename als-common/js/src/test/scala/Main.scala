import org.mulesoft.als.client.convert.LspConvertersClientToShared._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.documentsymbol.ClientSymbolKindClientCapabilities
import org.mulesoft.lsp.feature.documentsymbol.{SymbolKind, SymbolKindClientCapabilities}
import org.scalatest.FunSuite

class Main extends FunSuite {

  def main(args: Array[String]): Unit = {
    val s: SymbolKindClientCapabilities        = SymbolKindClientCapabilities(Set(SymbolKind.File))
    val s1: ClientSymbolKindClientCapabilities = s
    val s2: SymbolKindClientCapabilities       = s1

    println(s)
    println(s1)
    println(s2)
  }
}
