package com.example.userservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userRepository: UserRepository) {

    @GetMapping
    fun getAllUsers(): List<User> = userRepository.findAll()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userRepository.findById(id).orElse(null)
        return if (user != null) ResponseEntity(user, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> = ResponseEntity(userRepository.save(user), HttpStatus.CREATED)

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody newUser: User): ResponseEntity<User> {
        val existingUser = userRepository.findById(id).orElse(null)
        return if (existingUser != null) {
            val updatedUser = existingUser.copy(name = newUser.name, email = newUser.email)
            ResponseEntity(userRepository.save(updatedUser), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
