package com.example.cribswap.data.repo

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Handles uploading listing photos to Firebase Storage and
 * returning the public download URL to store in Firestore.
 *
 * Usage:
 *   val repo = ListingImageRepository()
 *   val url  = repo.uploadListingPhoto(userId, uri)  // returns download URL string
 */
class ListingImageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    /**
     * Upload a single photo for a listing.
     * @param userId   The current user's UID — used to namespace the file path.
     * @param imageUri The local URI chosen from the image picker.
     * @return         The public HTTPS download URL for the uploaded image.
     */
    suspend fun uploadListingPhoto(userId: String, imageUri: Uri): String {
        val filename = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference
            .child("listing_photos")
            .child(userId)
            .child(filename)

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    /**
     * Upload multiple photos and return all their download URLs.
     */
    suspend fun uploadListingPhotos(userId: String, uris: List<Uri>): List<String> =
        uris.map { uploadListingPhoto(userId, it) }

    /**
     * Delete a photo from Storage by its download URL.
     * Call this when a listing is deactivated or a photo is removed.
     */
    suspend fun deletePhoto(downloadUrl: String) {
        try {
            storage.getReferenceFromUrl(downloadUrl).delete().await()
        } catch (e: Exception) {
            // If the file doesn't exist, silently ignore
        }
    }
}