package cz.jablecnik.restservice.user

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.Size



@Entity
data class User (


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Size(min = 3)
    var name: String? = null,

    @Email
    var email: String? = null,

    @JsonIgnore
    var passwordHash: String? = null
)

