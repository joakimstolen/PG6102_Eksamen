package com.example.eksamen.usercollections

import com.example.eksamen.usercollections.db.UserService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MOMListener(
        private val bookedTripService: BookedTripService
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
}