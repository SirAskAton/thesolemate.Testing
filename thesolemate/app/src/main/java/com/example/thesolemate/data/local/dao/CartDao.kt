import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.thesolemate.data.local.entity.CartEntity
import com.example.thesolemate.data.local.entity.CartWithShoe

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    suspend fun getAll(): List<CartEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: CartEntity)

    @Update
    suspend fun update(cart: CartEntity)

    @Delete
    suspend fun delete(cart: CartEntity)

    @Query("DELETE FROM cart")
    suspend fun clear()

    @Transaction
    @Query("SELECT * FROM cart")
    suspend fun getCartWithShoes(): List<CartWithShoe>

    // ✅ Tambahan:
    @Query("UPDATE cart SET quantity = :newQty WHERE id = :cartId")
    suspend fun updateQuantity(cartId: Int, newQty: Int)

    @Query("DELETE FROM cart WHERE id = :cartId")
    suspend fun deleteById(cartId: Int)
}
