//import org.mulesoft.language.server.core.connections.IServerConnection
//import org.mulesoft.als.suggestions.interfaces.IExtendedFSProvider
//import org.mulesoft.als.suggestions.interfaces.IEditorStateProvider
//
//import scala.concurrent.Future
//
//class FsProvider(connection:IServerConnection) extends IExtendedFSProvider {
//
//  override def content(fullPath: String): Future[String]
//    = connection.content(fullPath)
//
//  override def resolve(absBasePath: String, path: String): Option[String]
//    = ???
//
//  override def dirName(fullPath: String): String = {
//    val lastSeparatorIndex = fullPath.lastIndexOf(platform.fs.separatorChar)
//
//    if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0) {
//      ""
//    } else {
//      fullPath.substring(0, lastSeparatorIndex)
//    }
//  }
//
//  override def existsAsync(path: String): Future[Boolean] = {
//    connection.exists(path)
//  }
//
//  override def isDirectory(fullPath: String): Boolean = ???
//
//  override def readDirAsync(path: String): Future[Seq[String]] = {
//
//    connection.readDir(path)
//  }
//
//  override def isDirectoryAsync(path: String): Future[Boolean] = {
//
//    connection.isDirectory(path)
//  }
//
//  def contentDirName(content: IEditorStateProvider): String = {
//
//    this.dirName(content.getPath)
//  }
//}
//
//object FsProvider {
//  def apply(connection:IServerConnection):FsProvider = new FsProvider(connection)
//}