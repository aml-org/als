## Java (LSP4J)

###### Server instantiation (runs in a separate thread)
The process uses the previously generated jar file: `sbt serverJVM/assembly`.
```Java
/**
 * Creates a Process with ALS running and trying to connect to the designated port
 */
public class ServerRunnable implements Runnable {
    private int port;
    public ServerRunnable(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        String[] args = {
                "java",
                "-jar",
                getClass().getClassLoader().getResource("als-server.jar").getPath(), // Path to the generated jar
                "--port",
                String.valueOf(port)
        };
        try {
            Process ps = Runtime.getRuntime().exec(args);
            ps.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
###### Client class with Diagnostics intercepted (for testing purposes)
```Java
public class DummyClient implements LanguageClient {

    private List<PublishDiagnosticsParams> diagnosticsList = new ArrayList<>();

    public List<PublishDiagnosticsParams> getDiagnostics() {
        return diagnosticsList;
    }

    synchronized private void addDiagnosticQueue(PublishDiagnosticsParams diagnosticsParams) {
        diagnosticsList.add(diagnosticsParams);
    }

    @Override
    public void telemetryEvent(Object object) { }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        addDiagnosticQueue(diagnostics); // Intercept diagnostics in order to later validate them in the Test
    }

    @Override
    public void showMessage(MessageParams messageParams) { }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {     }
}
```

###### Test suites (creates, connects, and initializes the client/server, and communicates with them)
```Java
public class ClientTest extends TestCase {

    /**
     * Creates a new thread which runs the server directly from ".jar", and initializes the corresponding Dummy Client
     * @param port: client socket port to which the server must connect
     * @return
     * @throws IOException, ExecutionException, InterruptedException
     */
    private Launcher<LanguageServer> getLanguageServerLauncher(int port) throws IOException, ExecutionException, InterruptedException {
        return getLanguageServerLauncher(new DummyClient(), port);
    }

    private Launcher<LanguageServer> getLanguageServerLauncher(DummyClient client, int port) throws IOException, ExecutionException, InterruptedException {
        final ServerSocket socket = new ServerSocket(port);
        final ServerRunnable thread = new ServerRunnable(port);
        new Thread(thread).start();
        final Socket usedSocket = socket.accept();

        final Launcher<LanguageServer> launcher =
                LSPLauncher.createClientLauncher(client, usedSocket.getInputStream(), usedSocket.getOutputStream());
        launcher.startListening();
        launcher.getRemoteProxy().initialize(new InitializeParams()).get();

        return launcher;
    }

    public void testServerCompletions() {
        try {
            final Launcher<LanguageServer> launcher = getLanguageServerLauncher(7770);
            final String uri = "file://uri.raml";
            String result = "";

            // Tell server that we just opened a new Document
            final DidOpenTextDocumentParams openParams =
                    new DidOpenTextDocumentParams(new TextDocumentItem(uri, "RAML", 0, ""));
            launcher.getRemoteProxy().getTextDocumentService().didOpen(openParams);

            // Ask server for suggestions in the empty RAML Document
            final CompletableFuture<Either<List<CompletionItem>, CompletionList>> completions =
                    launcher.getRemoteProxy().getTextDocumentService().completion(
                            new CompletionParams(new TextDocumentIdentifier(uri), new Position(0, 0)));

            // Assert received suggestions
            if (completions.get().isLeft()) {
                final List<CompletionItem> items = completions.get().getLeft();
                if(items.size() != 1)
                    Assert.fail("Should have returned 1 value [#%RAML 1.0] " + items.toString());
                result = items.get(0).getTextEdit().getNewText();
            } else {
                Assert.fail("No suggestions");
            }

            // Shutdown process
            launcher.getRemoteProxy().shutdown().get();

            assertEquals("#%RAML 1.0", result);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testServerStructure() {
        try {
            final Launcher<LanguageServer> launcher = getLanguageServerLauncher(7771);
            final String uri = "file://uri.raml";
            String result = "";

            // Tell server that we just opened a new Document
            final TextDocumentItem textDocument = new TextDocumentItem(uri, "RAML", 0, "#%RAML 1.0\ntitle: titulo\n");
            final DidOpenTextDocumentParams openParams = new DidOpenTextDocumentParams(textDocument);
            launcher.getRemoteProxy().getTextDocumentService().didOpen(openParams);

            final DocumentSymbolParams params = new DocumentSymbolParams(new TextDocumentIdentifier(uri));
            final CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> structure = launcher.getRemoteProxy().getTextDocumentService().documentSymbol(params);

            assert(structure.get().size() == 1 &&
                    "title".equals(structure.get().get(0).getRight().getName()));
            // Shutdown process
            launcher.getRemoteProxy().shutdown().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testServerValidations() {
        String result = "";
        try {
            final String uri = "file://uri.raml";
            final String content = "#%RAML 1.0\nversion: 1\n";
            final DummyClient client = new DummyClient();

            final Launcher<LanguageServer> launcher = getLanguageServerLauncher(client, 7772);
            final LanguageServer languageServer = launcher.getRemoteProxy();

            final TextDocumentItem textDocument = new TextDocumentItem(uri, "RAML", 0, content);
            final DidOpenTextDocumentParams openParams = new DidOpenTextDocumentParams(textDocument);
            languageServer.getTextDocumentService().didOpen(openParams);

            int counter = 100;
            while(client.getDiagnostics().isEmpty() && counter-- > 0){
                Thread.sleep(100);
            }

            languageServer.shutdown().get();
            if(client.getDiagnostics().isEmpty()){
                Assert.fail("TimeOut waiting for diagnostics");
            } else {
                assert(client.getDiagnostics().size() == 1 &&
                    client.getDiagnostics().get(0).getDiagnostics().size() == 1) &&
                    "API title is mandatory".equals(client.getDiagnostics().get(0).getDiagnostics().get(0).getMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testServer() {
        try {
            final Launcher<LanguageServer> launcher = getLanguageServerLauncher(7773);
            launcher.getRemoteProxy().shutdown().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        assert (true);
    }
}
