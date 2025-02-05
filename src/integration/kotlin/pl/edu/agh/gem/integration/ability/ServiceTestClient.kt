package pl.edu.agh.gem.integration.ability

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.test.web.servlet.client.MockMvcWebTestClient.bindToApplicationContext
import org.springframework.web.context.WebApplicationContext
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.headers.HeadersUtils.withAppContentType
import pl.edu.agh.gem.headers.HeadersUtils.withValidatedUser
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.paths.Paths.INTERNAL
import pl.edu.agh.gem.security.GemUser
import java.net.URI

@Component
@Lazy
class ServiceTestClient(applicationContext: WebApplicationContext) {
    private val webClient =
        bindToApplicationContext(applicationContext)
            .configureClient()
            .build()

    fun createGroup(
        body: Any,
        user: GemUser,
    ): ResponseSpec {
        return webClient.post()
            .uri(URI("$EXTERNAL/groups"))
            .headers { it.withValidatedUser(user).withAppContentType().withAppAcceptType() }
            .bodyValue(body)
            .exchange()
    }

    fun joinGroup(
        joinCode: String,
        user: GemUser,
    ): ResponseSpec {
        return webClient.post()
            .uri(URI("$EXTERNAL/groups/join/$joinCode"))
            .headers { it.withValidatedUser(user).withAppContentType().withAppAcceptType() }
            .exchange()
    }

    fun getMembers(groupId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$INTERNAL/members/$groupId"))
            .headers { it.withAppAcceptType() }
            .exchange()
    }

    fun getInternalGroup(groupId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$INTERNAL/groups/$groupId"))
            .headers { it.withAppAcceptType() }
            .exchange()
    }

    fun getUserGroups(user: GemUser): ResponseSpec {
        return webClient.get()
            .uri(URI("$EXTERNAL/groups"))
            .headers { it.withAppAcceptType().withValidatedUser(user) }
            .exchange()
    }

    fun getGroup(
        user: GemUser,
        groupId: String,
    ): ResponseSpec {
        return webClient.get()
            .uri(URI("$EXTERNAL/groups/$groupId"))
            .headers { it.withAppAcceptType().withValidatedUser(user) }
            .exchange()
    }

    fun getInternalUserGroups(userId: String): ResponseSpec {
        return webClient.get()
            .uri(URI("$INTERNAL/groups/users/$userId"))
            .headers { it.withAppAcceptType() }
            .exchange()
    }

    fun removeGroup(
        user: GemUser,
        groupId: String,
    ): ResponseSpec {
        return webClient.delete()
            .uri(URI("$EXTERNAL/groups/$groupId"))
            .headers { it.withValidatedUser(user) }
            .exchange()
    }

    fun updateGroup(
        body: Any,
        user: GemUser,
        groupId: String,
    ): ResponseSpec {
        return webClient.put()
            .uri(URI("$EXTERNAL/groups/$groupId"))
            .headers { it.withValidatedUser(user).withAppContentType().withAppAcceptType() }
            .bodyValue(body)
            .exchange()
    }
}
