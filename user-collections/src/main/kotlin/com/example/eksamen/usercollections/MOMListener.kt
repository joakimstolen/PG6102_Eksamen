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


    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(tripId: String) {

        bookedTripService.markAsCanceled(tripId)
        log.info("Canceled trip via MOM: $tripId")
    }
}