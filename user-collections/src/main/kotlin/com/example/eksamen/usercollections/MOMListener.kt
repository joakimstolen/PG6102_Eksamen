package com.example.eksamen.usercollections
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/MOMListener.kt
import com.example.eksamen.usercollections.db.UserService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MOMListener(
        private val bookedTripService: BookedTripService,
        private val userService: UserService
) {

    companion object{
        private val log = LoggerFactory.getLogger(MOMListener::class.java)
    }


    @RabbitListener(queues = ["#{deletionQueue.name}"])
    fun receiveDeleteFromAMQP(tripId: String) {

        bookedTripService.markAsCanceled(tripId)
        log.info("Canceled/Deleted trip via MOM: $tripId")
    }

    @RabbitListener(queues = ["#{creationQueue.name}"])
    fun receiveCreateFromAMQP(tripId: String) {

        log.info("Created trip via MOM: $tripId")
    }

    @RabbitListener(queues = ["#{authCreationQueue.name}"])
    fun receiveFromAMQP(userId: String) {

        val ok = userService.registerNewUser(userId)
        if(ok){
            log.info("Registered new user via MOM: $userId")
        }
    }
}