package com.example.eksamen.usercollections.db

import com.example.eksamen.usercollections.BookedTripService
import org.slf4j.LoggerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.LockModeType

@Repository
interface UserRepository : CrudRepository<User, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userId = :id")
    fun lockedFind(@Param("id") userId: String) : User?

}

@Service
@Transactional
class UserService(
        private val userRepository: UserRepository,
        private val bookedTripService: BookedTripService
) {

    companion object{
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }

    fun findByIdEager(userId: String) : User?{

        val user = userRepository.findById(userId).orElse(null)
        if(user != null){
            user.ownedBookedTrips.size
        }
        return user
    }

    fun registerNewUser(userId: String) : Boolean{

        if(userRepository.existsById(userId)){
            return false
        }

        val user = User()
        user.userId = userId
        //Signup bonus
        user.coins = 100
        userRepository.save(user)
        return true
    }


    private fun validateTrip(tripId: String) {
        if (!bookedTripService.isInitialized()) {
            throw IllegalStateException("Booked trip service is not initialized")
        }

        if (!bookedTripService.tripCollection.any { it.tripId == tripId }) {
            throw IllegalArgumentException("Invalid tripId: $tripId")
        }
    }

    private fun validateUser(userId: String) {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User $userId does not exist")
        }
    }

    private fun validate(userId: String, cardId: String) {
        validateUser(userId)
        validateTrip(cardId)
    }


    fun bookTrip(userId: String, tripId: String, nrOfPeople: Int)  {
        //validate(userId, tripId)

        bookedTripService.fetchData(tripId)

        //val price = bookedTripService.price(tripId)
        val user = userRepository.lockedFind(userId)!!


        //if (user.coins < price) {
        //    throw IllegalArgumentException("Not enough coins")
       // }

        //user.coins -= price

        user.nrOfPersons += nrOfPeople


        addTrip(user, tripId, nrOfPeople)
    }


    private fun addTrip(user: User, tripId: String, nrOfPeople: Int) {
        user.ownedBookedTrips.find { it.tripId == tripId }
                ?.apply { numberOfTrips++ }
                ?: BookedTrip().apply {
                    this.tripId = tripId
                    this.user = user
                    this.numberOfPeopleBooked = nrOfPeople
                    this.numberOfTrips = 1
                }.also { user.ownedBookedTrips.add(it) }
    }

    fun cancelTrip(userId: String, tripId: String) {
        //validate(userId, tripId)

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedBookedTrips.find { it.tripId == tripId }
        if(copy == null || copy.numberOfTrips == 0){
            throw IllegalArgumentException("User $userId does not own this $tripId")
        }

        copy.numberOfTrips--

    }

    fun alterTrip(userId: String, tripId: String, nrOfPeople: Int) {
        //validate(userId, tripId)

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedBookedTrips.find { it.tripId == tripId }
        if(copy == null || copy.numberOfTrips == 0){
            throw IllegalArgumentException("User $userId does not own this $tripId")
        }

        user.nrOfPersons = nrOfPeople

    }

    fun markAsCanceled(userId: String, tripId: String){
        //validate(userId, tripId)

        val user = userRepository.lockedFind(userId)!!
        val copy = user.ownedBookedTrips.find { it.tripId == tripId }
        if (copy != null) {
            copy.tripId = "CANCELED"
        }

    }




}