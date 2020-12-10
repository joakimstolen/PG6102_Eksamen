package com.example.eksamen.trip.service

import com.example.eksamen.trip.db.TripEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface TripRepository : CrudRepository<TripEntity, String> {


}


@Service
class TripService (private var tripRepository: TripRepository, val em : EntityManager){

    fun registerNewTrip(tripId: String) : Boolean{
        if(tripRepository.existsById(tripId)){
            return false
        }

        val trip = TripEntity(tripId, "place", 1, 1000)
        tripRepository.save(trip)
        return true
    }


    fun getNextPage(size: Int, keysetId: String? = null, keysetPrice: Int? = null): List<TripEntity>{
        if(size < 1 || size > 1000){
            throw IllegalArgumentException("Invalid size value: $size")
        }


        if ((keysetId==null && keysetPrice!=null) || (keysetId!=null && keysetPrice==null)){
            throw IllegalArgumentException("keysetId and keysetPrice should be both missing, or both present")
        }

        val query: TypedQuery<TripEntity>
        if (keysetId == null){
            query = em.createQuery(
                    "select t from TripEntity t order by t.pricePerPerson DESC, t.tripId DESC", TripEntity::class.java
            )
        } else {
            query = em.createQuery(
                    "select t from TripEntity t where t.pricePerPerson<?2 or (t.pricePerPerson=?2 and t.tripId<?1) order by t.pricePerPerson DESC, t.tripId DESC", TripEntity::class.java
            )

            query.setParameter(1, keysetId)
            query.setParameter(2, keysetPrice)
        }

        query.maxResults = size

        return query.resultList


    }


}