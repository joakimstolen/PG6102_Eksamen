package com.example.eksamen.trip.db

import com.example.eksamen.trip.service.TripRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Profile("FakeData")
@Service
@Transactional
class FakeDataService (
        val tripRepository: TripRepository
){

    @PostConstruct
    fun init(){
        val trip1 = TripEntity("t001", "Oslo", 5, 2000)
        val trip2 = TripEntity("t002", "Bergen", 3, 900)
        val trip3 = TripEntity("t003", "Trondheim", 7, 1800)
        val trip4 = TripEntity("t004", "Rome", 5, 2300)
        val trip5 = TripEntity("t005", "London", 8, 3400)
        val trip6 = TripEntity("t006", "New York", 10, 5400)
        val trip7 = TripEntity("t007", "Madrid", 2, 1800)
        val trip8 = TripEntity("t008", "Barcelona", 7, 4600)
        val trip9 = TripEntity("t009", "Paris", 9, 5000)
        val trip10 = TripEntity("t010", "Stockholm", 6, 2950)
        val trip11 = TripEntity("t011", "Copenhagen", 4, 2700)
        val trip12 = TripEntity("t012", "Prague", 5, 2300)
        val trip13 = TripEntity("t013", "Budapest", 7, 3600)
        val trip14 = TripEntity("t014", "Helsinki", 10, 4250)
        val trip15 = TripEntity("t015", "Warsaw", 7, 1250)
        val trip16 = TripEntity("t016", "Riga", 2, 850)

        tripRepository.save(trip1)
        tripRepository.save(trip2)
        tripRepository.save(trip3)
        tripRepository.save(trip4)
        tripRepository.save(trip5)
        tripRepository.save(trip6)
        tripRepository.save(trip7)
        tripRepository.save(trip8)
        tripRepository.save(trip9)
        tripRepository.save(trip10)
        tripRepository.save(trip11)
        tripRepository.save(trip12)
        tripRepository.save(trip13)
        tripRepository.save(trip14)
        tripRepository.save(trip15)
        tripRepository.save(trip16)
    }
}