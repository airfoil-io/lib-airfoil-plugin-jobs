import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

data class RepositorySpec(
    val uri: String,
) {

    fun maven(handler: RepositoryHandler, gprUser: String, gprKey: String): MavenArtifactRepository {
        val spec = this
        return handler.maven {
            setUrl(spec.uri)
            credentials {
                username = gprUser
                password = gprKey
            }
        }
    }
}
