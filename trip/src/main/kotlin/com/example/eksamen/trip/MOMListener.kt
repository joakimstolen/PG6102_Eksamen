package com.example.eksamen.trip

import com.example.eksamen.trip.service.TripService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MOMListener(
        private val tripService: TripService
) {

    companion object{
        private val log = LoggerFactory.getLogger(MOMListener::class.java)
    }

    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(tripId: String) {
        val ok = tripService.registerNewTrip(tripId)
        if(ok){
            log.info("Registered new trips as ADMIN via MOM: $tripId")
        }
    }

}