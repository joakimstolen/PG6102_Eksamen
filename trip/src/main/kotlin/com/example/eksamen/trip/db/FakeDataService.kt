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
        val trip17 = TripEntity("t017", "Beijing", 2, 4500)
        val trip18 = TripEntity("t018", "Tokyo", 9, 7800)
        val trip19 = TripEntity("t019", "Bangkok", 10, 6400)
        val trip20 = TripEntity("t020", "Los Angeles", 12, 8300)
        val trip21 = TripEntity("t021", "Washington DC", 5, 3900)
        val trip22 = TripEntity("t022", "Amsterdam", 2, 1200)
        val trip23 = TripEntity("t023", "Minsk", 8, 2000)
        val trip24 = TripEntity("t024", "Moscow", 8, 4100)
        val trip25 = TripEntity("t025", "Manchester", 2, 1200)
        val trip26 = TripEntity("t026", "New Dehli", 13, 7800)
        val trip27 = TripEntity("t027", "Mallorca", 4, 1650)

        if(tripRepository.count().toInt() == 0){
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
            tripRepository.save(trip17)
            tripRepository.save(trip18)
            tripRepository.save(trip19)
            tripRepository.save(trip20)
            tripRepository.save(trip21)
            tripRepository.save(trip22)
            tripRepository.save(trip23)
            tripRepository.save(trip24)
            tripRepository.save(trip25)
            tripRepository.save(trip26)
            tripRepository.save(trip27)
        }

    }
}