package amf.core.client.common.validation

import amf.core.internal.remote._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

// delete as soon as Async26Profile is created on amf-side
@JSExportAll
@JSExportTopLevel("ProfileNames")
object ProfileNames {
  val AMF: ProfileName                = AmfProfile
  val OAS20: ProfileName              = Oas20Profile
  val OAS30: ProfileName              = Oas30Profile
  val RAML10: ProfileName             = Raml10Profile
  val RAML08: ProfileName             = Raml08Profile
  val ASYNC: ProfileName              = AsyncProfile
  val ASYNC20: ProfileName            = Async20Profile
  val ASYNC26: ProfileName            = Async26Profile
  val AML: ProfileName                = AmlProfile
  val PAYLOAD: ProfileName            = PayloadProfile
  val GRPC: ProfileName               = GrpcProfile
  val GRAPHQL: ProfileName            = GraphQLProfile
  val GRAPHQL_FEDERATION: ProfileName = GraphQLFederationProfile
  val JSONSCHEMA: ProfileName         = JsonSchemaProfile

  lazy val specProfiles: Seq[ProfileName] =
    Seq(
      AmfProfile,
      Oas20Profile,
      Oas30Profile,
      Raml08Profile,
      Raml10Profile,
      AsyncProfile,
      Async20Profile,
      Async26Profile,
      GraphQLProfile,
      GraphQLFederationProfile,
      GrpcProfile,
      JsonSchemaProfile,
      AvroSchemaProfile
    )
}

@JSExportAll
case class ProfileName(private[amf] val p: String, private val m: MessageStyle = AMFStyle) {
  @JSExportTopLevel("ProfileName")
  def this(profile: String) = this(profile, AMFStyle)
  def profile: String            = p
  def messageStyle: MessageStyle = m
  override def toString: String  = p
  def isOas(): Boolean           = false
  def isRaml(): Boolean          = false
}

object AmfProfile     extends ProfileName(Amf.id)
object AmlProfile     extends ProfileName(Aml.id)
object UnknownProfile extends ProfileName("")

object Oas20Profile extends ProfileName(Oas20.id, OASStyle) {
  override def isOas(): Boolean = true
}

object Oas30Profile extends ProfileName(Oas30.id, OASStyle) {
  override def isOas(): Boolean = true
}

object Raml08Profile extends ProfileName(Raml08.id, RAMLStyle) {
  override def isRaml(): Boolean = true
}

object Raml10Profile extends ProfileName(Raml10.id, RAMLStyle) {
  override def isRaml(): Boolean = true
}

object AsyncProfile   extends ProfileName(AsyncApi.id, OASStyle)
object Async20Profile extends ProfileName(AsyncApi20.id, OASStyle)
object Async26Profile extends ProfileName(AsyncApi26.id, OASStyle)
object PayloadProfile extends ProfileName(Payload.id)

object GrpcProfile extends ProfileName(Grpc.id, AMFStyle) {
  override def isRaml(): Boolean = false
}

object GraphQLProfile extends ProfileName(GraphQL.id, AMFStyle) {
  override def isOas(): Boolean  = false
  override def isRaml(): Boolean = false
}

object GraphQLFederationProfile extends ProfileName(GraphQLFederation.id, AMFStyle) {
  override def isOas(): Boolean  = false
  override def isRaml(): Boolean = false
}

object JsonSchemaProfile extends ProfileName(JsonSchema.id, AMFStyle) {
  override def isOas(): Boolean  = false
  override def isRaml(): Boolean = false
}

object AvroSchemaProfile extends ProfileName(AvroSchema.id, AMFStyle) {
  override def isOas(): Boolean  = false
  override def isRaml(): Boolean = false
}

object ProfileName {
  def unapply(name: String): Option[ProfileName] =
    name match {
      case AmfProfile.p               => Some(AmfProfile)
      case Oas30Profile.p             => Some(Oas30Profile)
      case Raml08Profile.p            => Some(Raml08Profile)
      case AsyncProfile.p             => Some(AsyncProfile)
      case Async20Profile.p           => Some(Async20Profile)
      case Async26Profile.p           => Some(Async26Profile)
      case GrpcProfile.p              => Some(GrpcProfile)
      case GraphQLProfile.p           => Some(GraphQLProfile)
      case GraphQLFederationProfile.p => Some(GraphQLFederationProfile)
      case JsonSchemaProfile.p        => Some(JsonSchemaProfile)
      case AvroSchemaProfile.p        => Some(AvroSchemaProfile)
      case _                          => None
    }

  def apply(profile: String): ProfileName = profile match {
    case Amf.id               => AmfProfile
    case "OAS" | Oas20.id     => Oas20Profile  // for compatibility
    case Oas30.id             => Oas30Profile
    case Raml08.id            => Raml08Profile
    case "RAML" | Raml10.id   => Raml10Profile // for compatibility
    case AsyncApi.id          => AsyncProfile
    case AsyncApi20.id        => Async20Profile
    case AsyncApi21.id        => Async20Profile
    case AsyncApi22.id        => Async20Profile
    case AsyncApi23.id        => Async20Profile
    case AsyncApi24.id        => Async20Profile
    case AsyncApi25.id        => Async20Profile
    case AsyncApi26.id        => Async26Profile
    case Grpc.id              => GrpcProfile
    case GraphQL.id           => GraphQLProfile
    case GraphQLFederation.id => GraphQLFederationProfile
    case JsonSchema.id        => JsonSchemaProfile
    case AvroSchema.id        => AvroSchemaProfile
    case custom               => new ProfileName(custom)
  }
}

object MessageStyle {
  def apply(name: String): MessageStyle = name match {
    case Raml10.id | Raml08.id => RAMLStyle
    case Oas20.id | Oas30.id   => OASStyle
    case AsyncApi.id | AsyncApi20.id | AsyncApi21.id | AsyncApi22.id | AsyncApi23.id | AsyncApi24.id | AsyncApi25.id |
        AsyncApi26.id =>
      OASStyle
    case _ => AMFStyle
  }
}

@JSExportAll
trait MessageStyle {
  def profileName: ProfileName
}

@JSExportAll
@JSExportTopLevel("MessageStyles")
object MessageStyles {
  val RAML: MessageStyle  = RAMLStyle
  val OAS: MessageStyle   = OASStyle
  val ASYNC: MessageStyle = AsyncStyle
  val AMF: MessageStyle   = AMFStyle
}

object RAMLStyle extends MessageStyle {
  override def profileName: ProfileName = Raml10Profile
}
object OASStyle extends MessageStyle {
  override def profileName: ProfileName = Oas20Profile
}

object AsyncStyle extends MessageStyle {
  override def profileName: ProfileName = AsyncProfile
}

object AMFStyle extends MessageStyle {
  override def profileName: ProfileName = AmfProfile
}
