package cz.jablecnik.restservice.user

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size



@Entity
data class User (


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Size(min = 3)
    @Column(nullable = false)
    var name: String? = null,

    @Email
    @Column(nullable = false)
    var email: String? = null,

    @NotNull
    @JsonIgnore
    @Column(nullable = false)
    var passwordHash: String? = null
)

