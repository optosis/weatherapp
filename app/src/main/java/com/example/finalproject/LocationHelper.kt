import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.io.IOException
import java.util.*

class LocationHelper(private val context: Context) {

    fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                // You can customize the format of the location name as needed
                return "${address.locality}, ${address.countryName}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Location not found"
    }
}
