package com.example.eksamen.usercollections.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/db/UserService.kt
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.usercollections.BookedTripService
import org.slf4j.LoggerFactory
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


    fun bookTrip(userId: String, tripId: String, nrOfPeople: Int)  {

        //checking if trip exists in trips api
        val fetchTripId: TripDto? = bookedTripService.fetchData(tripId)

        val tripIdFromFetch = fetchTripId?.tripId


        //val price = bookedTripService.price(tripId)
        val user = userRepository.lockedFind(userId)!!


        //user.coins -= price

        user.nrOfPersons += nrOfPeople


        //sending user, tripId from the trip api and nr of people to be booked
        addTrip(user, tripIdFromFetch, nrOfPeople)
    }


    private fun addTrip(user: User, tripId: String?, nrOfPeople: Int) {
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

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedBookedTrips.find { it.tripId == tripId }
        if(copy == null || copy.numberOfTrips == 0){
            throw IllegalArgumentException("User $userId does not own this $tripId")
        }

        copy.numberOfTrips--

    }

    fun alterTrip(userId: String, tripId: String, nrOfPeople: Int) {

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedBookedTrips.find { it.tripId == tripId }
        if(copy == null || copy.numberOfTrips == 0){
            throw IllegalArgumentException("User $userId does not own this $tripId")
        }

        user.nrOfPersons = nrOfPeople

    }






}