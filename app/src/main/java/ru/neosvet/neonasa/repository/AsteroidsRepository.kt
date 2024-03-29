package ru.neosvet.neonasa.repository

import ru.neosvet.neonasa.app.App
import ru.neosvet.neonasa.repository.room.AsteroidEntity
import ru.neosvet.neonasa.repository.room.GroupEntity
import java.text.SimpleDateFormat

class AsteroidsRepository {
    private val base = App.getBase()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    fun getAsteroid(name: String) = base.asteroidDao().get(name)
    fun getGroup(id: Long) = base.groupDao().get(id)
    fun getGroups() = base.groupDao().getList()
    fun getGroupList(id: Long) = base.asteroidDao().getDateList(id)
    fun getMarkedList() = base.asteroidDao().getMarkedList()
    fun getFilteredList(filter: String) = base.asteroidDao().getFilteredList(filter)

    private var lastDate: Long = 0
    private var positionAsteroid = 0

    fun addGroup(item: String) {
        lastDate = dateFormat.parse(item).time
        base.groupDao().add(
            GroupEntity(date = lastDate, title = item)
        )
        positionAsteroid = 0
    }

    fun addAsteroid(item: Asteroid) {
        var distance: Int? = null
        item.closeApproachData.forEach {
            if (it.orbitingBody.equals("Earth")) {
                distance = distanceToInt(it.missDistance.kilometers)
            }
        }

        val asteroid = base.asteroidDao().get(item.name)

        base.asteroidDao().add(
            AsteroidEntity(
                name = item.name,
                link = item.nasaJplUrl,
                updated = lastDate,
                diameter_min = item.estimatedDiameter.meters.estimatedDiameterMin.toFloat(),
                diameter_max = item.estimatedDiameter.meters.estimatedDiameterMax.toFloat(),
                distance = distance,
                position = positionAsteroid,
                note = asteroid?.note,
                priority = asteroid?.priority ?: detectPriority(distance),
                marked = asteroid?.marked ?: false
            )
        )
        positionAsteroid++
    }

    private fun detectPriority(distance: Int?): Int {
        if (distance == null)
            return 0
        var d = (distance / 10E5).toInt()
        if (d < 1) return 10
        d = 9 - d / 5
        if (d < 0) return 0
        return d
    }

    private fun distanceToInt(value: String): Int {
        val i = value.indexOf(".")
        if (i > 0)
            return value.substring(0, i).toInt()
        else
            return value.toInt()
    }

    fun update(asteroid: AsteroidEntity) {
        base.asteroidDao().update(asteroid)
    }

    fun updatePosition(asteroid: String, position: Int) {
        val a = getAsteroid(asteroid) ?: return
        a.position = position
        base.asteroidDao().update(a)
    }

    fun removeAsterod(asteroid: AsteroidEntity) {
        base.asteroidDao().delete(asteroid)
    }
}