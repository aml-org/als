package org.mulesoft.als.server.workspace

// TODO: when implemented Validation Profile and Semantic Extension, assert in tests the mutability of AmfConfiguration
//   for example, start test, register/unregister dialect, check that the resulting unit still has the starting dialects
//class WorkspaceConfigurationTest extends LanguageServerBaseTest with ChangesWorkspaceConfiguration {
//  override def rootPath: String = ""
//
//  implicit override def executionContext: ExecutionContext =
//    scala.concurrent.ExecutionContext.Implicits.global
//  implicit val p: Platform         = platform
//  private val mainApiUri           = "file://folder/api.raml"
//  private val isolatedUri          = "file://folder/isolated.raml"
//  private val exchangeUri          = "file://folder/exchange.json"
//  private val validationProfileUri = "file://folder/profile.yaml"
//  val api                          = "#%RAML 1.0\ntitle: test\n"
//  val isolated                     = "#%RAML 1.0\ntitle: test2\n"
//  val validationProfile            = "#%Validation Profile 1.0\nprofile: MyProfile"
//  val exchange                     = "{\n  \"main\": \"api.raml\"\n}"
//  private val extensionUri         = "file://folder/extension.yaml"
//  private val extensionContent     = """#%Dialect 1.0
//                                                    |dialect: Annotation mappings
//                                                    |version: 1.0
//                                                    |
//                                                    |external:
//                                                    |  aml: http://a.ml/vocab#
//                                                    |  apicontract: http://a.ml/vocabularies/apiContract#
//                                                    |
//                                                    |documents:
//                                                    |  root:
//                                                    |    encodes: string # just necessary to avoid errors, won't really do anything
//                                                    |
//                                                    |annotationMappings:
//                                                    |  RateLimitingAnnotationMapping:
//                                                    |    domain: apicontract.WebAPI
//                                                    |    propertyTerm: aml.rate-limit
//                                                    |    range: integer
//                                                    |    minimum: 0
//                                                    |    maximum: 10000
//                                                    |    mandatory: true
//                                                    |
//                                                    |  MaintainerAnnotationMapping:
//                                                    |    domain: apicontract.API
//                                                    |    propertyTerm: aml.maintainer
//                                                    |    range:  PersonNodeMapping
//                                                    |
//                                                    |nodeMappings:
//                                                    |  PersonNodeMapping:
//                                                    |    mapping:
//                                                    |      name:
//                                                    |        range: string
//                                                    |      surname:
//                                                    |        range: string
//                                                    |      email:
//                                                    |        range: string
//                                                    |
//                                                    |extensions:
//                                                    |  maintainer: MaintainerAnnotationMapping
//                                                    |  rateLimiting: RateLimitingAnnotationMapping""".stripMargin
//
//  def rl(withExchangeFile: Boolean): ResourceLoader = new ResourceLoader {
//
//    /** Fetch specified resource and return associated content. Resource should have been previously accepted. */
//    override def fetch(resource: String): Future[Content] = {
//      val content =
//        if (resource == mainApiUri) api
//        else if (resource == isolatedUri) isolated
//        else if (resource == validationProfileUri) validationProfile
//        else if (withExchangeFile && resource == exchangeUri) exchange
//        else if (resource == extensionUri) extensionContent
//        else throw new ResourceNotFound("Not found: " + resource)
//
//      Future.successful(new Content(content, resource))
//    }
//
//    /** Accepts specified resource. */
//    override def accepts(resource: String): Boolean =
//      resource == mainApiUri ||
//        (withExchangeFile && resource == exchangeUri) ||
//        resource == isolatedUri ||
//        resource == extensionUri ||
//        resource == validationProfileUri
//  }
//
//  test("Unit from main tree should contain configuration") {
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory(amfConfiguration)
//      val workspaceManager: WorkspaceManager           = factory.workspaceManager
//      val server                                       = buildServer(factory)
//      for {
//        _    <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//        _    <- listener.nextCall // parse main file
//        unit <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
//      } yield {
//        assert(unit.mainFile.contains(mainApiUri))
//        assert(unit.context.projectInfo.config.mainFile.contains("api.raml"))
//      }
//    })
//  }
//
//  test("Isolated unit should contain configuration") {
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory(amfConfiguration)
//      val workspaceManager: WorkspaceManager           = factory.workspaceManager
//      val server                                       = buildServer(factory)
//      for {
//        _    <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//        _    <- listener.nextCall // parse main file
//        _    <- openFileNotification(server)(isolatedUri, isolated)
//        _    <- listener.nextCall // parse isolated
//        unit <- workspaceManager.getUnit(isolatedUri, UUID.randomUUID().toString)
//      } yield {
//        assert(unit.mainFile.isEmpty)
//        assert(unit.context.projectInfo.config.mainFile.contains("api.raml"))
//      }
//    })
//
//  }
//  test("Should update the configuration by command for new Units") {
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, parserListener) = createPatchedWorkspaceManagerFactory(amfConfiguration)
//      val workspaceManager: WorkspaceManager                 = factory.workspaceManager
//      val initialArgs                                        = changeConfigArgs(Some(mainApiUri))
//      val args                                               = changeConfigArgs(Some(isolatedUri))
//      val server                                             = buildServer(factory)
//      for {
//        _          <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//        _          <- changeWorkspaceConfiguration(workspaceManager, initialArgs)
//        _          <- parserListener.nextCall // parse main file
//        _          <- openFileNotification(server)(isolatedUri, isolated)
//        _          <- parserListener.nextCall // parse isolated
//        firstUnit  <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
//        _          <- changeWorkspaceConfiguration(workspaceManager, args)
//        _          <- parserListener.nextCall
//        _          <- openFileNotification(server)(mainApiUri, isolated)
//        _          <- parserListener.nextCall
//        secondUnit <- workspaceManager.getUnit(mainApiUri, UUID.randomUUID().toString)
//        thirdUnit  <- workspaceManager.getUnit(isolatedUri, UUID.randomUUID().toString)
//      } yield {
//        assert(firstUnit.mainFile.contains(mainApiUri))
//        assert(firstUnit.context.projectInfo.config.mainFile.contains("api.raml"))
//
//        assert(secondUnit.mainFile.isEmpty)
//        assert(secondUnit.context.projectInfo.config.mainFile.contains("isolated.raml"))
//
//        assert(thirdUnit.mainFile.contains(isolatedUri))
//        assert(thirdUnit.context.projectInfo.config.mainFile.contains("isolated.raml"))
//      }
//    })
//
//  }
//
//  test("Should notify project dependencies the configuration used") {
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, listener) = createPatchedWorkspaceManagerFactory(amfConfiguration)
//      val server                                       = buildServer(factory)
//      for {
//        _              <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//        mainFileResult <- listener.nextCall
//        _              <- openFileNotification(server)(isolatedUri, isolated)
//        isolatedResult <- listener.nextCall
//      } yield {
//        assert(!isolatedResult.tree)
//        assert(isolatedResult.parseResult.context.projectInfo.config.mainFile.contains("api.raml"))
//        assert(isolatedResult.parseResult.context.projectInfo.config.mainFile.contains("api.raml"))
//
//        assert(mainFileResult.tree)
//        assert(mainFileResult.parseResult.context.projectInfo.config.mainFile.contains("api.raml"))
//      }
//    })
//  }
//
//  test("Should notify resolution dependencies the configuration used") {
//    val listener = new MockResolutionListener(logger)
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, _) =
//        createPatchedWorkspaceManagerFactory(amfConfiguration, List.empty, List(listener))
//      val server = buildServer(factory)
//      for {
//        _              <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//        mainFileResult <- listener.nextCall
//        _              <- openFileNotification(server)(isolatedUri, isolated)
//        isolatedResult <- listener.nextCall
//      } yield {
//        assert(isolatedResult.projectInfo.config.mainFile.contains("api.raml"))
//        assert(mainFileResult.projectInfo.config.mainFile.contains("api.raml"))
//      }
//    })
//  }
//
//  def getWorkspaceConfiguration(server: LanguageServer, uri: String): Future[GetWorkspaceConfigurationResult] =
//    server
//      .resolveHandler(GetWorkspaceConfigurationRequestType)
//      .get(GetWorkspaceConfigurationParams(TextDocumentIdentifier(uri)))
//
//  test("Get workspace notification request should return current configuration") {
//    val listener = new MockResolutionListener(logger)
//    AmfConfigurationWrapper(Seq(rl(true))).flatMap(amfConfiguration => {
//      val (factory: WorkspaceManagerFactory, _) =
//        createPatchedWorkspaceManagerFactory(amfConfiguration, List.empty, List(listener))
//      val args             = changeConfigArgs(Some(isolatedUri))
//      val args2            = changeConfigArgs(Some(isolatedUri), None, Set.empty, Set(validationProfileUri))
//      val args3            = changeConfigArgs(Some(isolatedUri), None, Set("dependency.yaml"))
//      val args4            = changeConfigArgs(Some(isolatedUri), None, Set.empty, Set.empty, Set(extensionUri))
//      val workspaceManager = factory.workspaceManager
//      withServer[Assertion](buildServer(factory)) {
//        server =>
//          for {
//            _          <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"file://folder")))
//            config1    <- getWorkspaceConfiguration(server, mainApiUri)
//            _          <- changeWorkspaceConfiguration(workspaceManager, args)
//            _          <- listener.nextCall
//            config2    <- getWorkspaceConfiguration(server, mainApiUri)
//            _          <- changeWorkspaceConfiguration(workspaceManager, args2)
//            _          <- listener.nextCall
//            config3    <- getWorkspaceConfiguration(server, mainApiUri)
//            _          <- changeWorkspaceConfiguration(workspaceManager, args3)
//            _          <- listener.nextCall
//            config4    <- getWorkspaceConfiguration(server, mainApiUri)
//            _          <- changeWorkspaceConfiguration(workspaceManager, args4)
//            config5    <- getWorkspaceConfiguration(server, mainApiUri)
//            registered <- workspaceManager.getWorkspace(mainApiUri).map(_.registeredDialects)
//          } yield {
//            def customValidationProfiles(config: GetWorkspaceConfigurationResult) =
//              config.configuration.dependencies
//                .filter(f => f.isRight && f.right.exists(_.scope == CUSTOM_VALIDATION))
//                .map(_.right.get.file)
//            def semanticExtensions(config: GetWorkspaceConfigurationResult) =
//              config.configuration.dependencies
//                .filter(f => f.isRight && f.right.exists(_.scope == SEMANTIC_EXTENSION))
//                .map(_.right.get.file)
//            def dependencies(config: GetWorkspaceConfigurationResult) =
//              config.configuration.dependencies.flatMap {
//                case Left(value) => Some(value)
//                case Right(value) if !Set(CUSTOM_VALIDATION, SEMANTIC_EXTENSION).contains(value.scope) =>
//                  Some(value.file)
//                case _ => None
//              }
//            assert(config1.workspace == """file://folder""")
//            assert(config2.workspace == """file://folder""")
//            assert(config3.workspace == """file://folder""")
//            assert(config4.workspace == """file://folder""")
//            assert(config1.configuration.mainPath.isEmpty)
//            assert(config2.configuration.mainPath.contains("isolated.raml"))
//            assert(config3.configuration.mainPath.contains("isolated.raml"))
//            assert(config4.configuration.mainPath.contains("isolated.raml"))
//            assert(customValidationProfiles(config1).isEmpty)
//            assert(customValidationProfiles(config2).isEmpty)
//            assert(customValidationProfiles(config3).contains(validationProfileUri))
//            assert(config1.configuration.dependencies.isEmpty)
//            assert(config2.configuration.dependencies.isEmpty)
//            assert(dependencies(config3).isEmpty)
//            assert(dependencies(config4).contains("dependency.yaml"))
//            assert(semanticExtensions(config1).isEmpty)
//            assert(semanticExtensions(config2).isEmpty)
//            assert(semanticExtensions(config3).isEmpty)
//            assert(semanticExtensions(config5).contains(extensionUri))
//            assert(registered.flatMap(_.location()).contains(extensionUri))
//          }
//      }
//    })
//  }
//
//  def createPatchedWorkspaceManagerFactory(amfConfiguration: AmfConfigurationWrapper,
//                                           projectDependencies: List[BaseUnitListener] = List.empty,
//                                           resolutionDependencies: List[ResolvedUnitListener] = List.empty)
//    : (WorkspaceManagerFactory, MockParseListener) = {
//
//    val clientNotifier                       = new MockDiagnosticClientNotifier
//    val telemetryManager: TelemetryManager   = new TelemetryManager(clientNotifier, logger)
//    val directoryResolver: DirectoryResolver = new PlatformDirectoryResolver(platform)
//    val parserListener                       = new MockParseListener()
//    (WorkspaceManagerFactory(
//       projectDependencies :+ parserListener,
//       resolutionDependencies,
//       telemetryManager,
//       directoryResolver,
//       logger,
//       new ConfigurationManager(),
//       None,
//       None
//     ),
//     parserListener)
//  }
//
//  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
//    new LanguageServerBuilder(factory.documentManager,
//                              factory.workspaceManager,
//                              factory.configurationManager,
//                              factory.resolutionTaskManager)
//      .addRequestModule(factory.workspaceConfigurationManager)
//      .build()
//
//  class MockResolutionListener(override val logger: Logger)
//      extends ResolvedUnitListener
//      with AbstractTestClientNotifier[AmfResolvedUnit]
//      with TimeoutFuture {
//    override type RunType = CallbackRunnable
//
//    override protected def runnable(ast: AmfResolvedUnit, uuid: String): CallbackRunnable =
//      new CallbackRunnable(ast.baseUnit.id, ast, this)
//
//    override protected def onSuccess(uuid: String, uri: String): Unit =
//      logger.log(s"success: $uri, uuid: $uuid", MessageSeverity.DEBUG, "MockResolutionListener", "onSuccess")
//
//    override protected def onFailure(uuid: String, uri: String, t: Throwable): Unit =
//      logger.log(s"Failed: $uri, uuid: $uuid", MessageSeverity.ERROR, "MockResolutionListener", "onFailure")
//
//    override protected def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit =
//      logger.debug("notified", "MockResolutionListener", "onNewAstPreprocess")
//
//    override def onRemoveFile(uri: String): Unit = {}
//
//    override def nextCall: Future[AmfResolvedUnit] = timeoutFuture(super.nextCall, 1000)
//
//    class CallbackRunnable(val uri: String, ast: AmfResolvedUnit, callback: MockResolutionListener)
//        extends Runnable[Unit] {
//      val kind      = "CallbackRunnable"
//      var cancelled = false
//      override def run(): Promise[Unit] = {
//        callback.notify(ast)
//        Promise.successful()
//      }
//
//      override def conflicts(other: Runnable[Any]): Boolean =
//        other.asInstanceOf[CallbackRunnable].kind == kind && uri == other
//          .asInstanceOf[CallbackRunnable]
//          .uri
//
//      override def cancel(): Unit = cancelled = true
//
//      override def isCanceled(): Boolean = cancelled
//    }
//  }
//
//}
