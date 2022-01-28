package bista.shiddarth.volley.controller

import bista.shiddarth.volley.model.Bank
import bista.shiddarth.volley.service.BankService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


@Validated
@RestController
@RequestMapping("api/banks")
class BankController(private val service: BankService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message,HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message,HttpStatus.BAD_REQUEST)

    @ExceptionHandler(value = [ConstraintViolationException::class])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun onConstraintViolationException(e: ConstraintViolationException): String? {
        log.info("In method onConstraintViolation")
        val violations = e.constraintViolations
        val strBuilder = StringBuilder()
        for (violation in violations) {
            strBuilder.append(
                """
                Account number ${violation.message}
                
                """.trimIndent()
            )
        }
        return strBuilder.toString()
    }


    @GetMapping
    fun getBanks(): Collection<Bank> = service.getBanks()

    @GetMapping("/{accountNumber}")
    fun getBank(@PathVariable @Size(max = 6) @Pattern(regexp = "^([SW])\\w+([0-9]{4})\$", message = "Account number must match Regex")  accountNumber: String) = service.getBank(accountNumber)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBank(@RequestBody bank: Bank): Bank = service.addBank(bank)

    @PatchMapping
    fun updateBank(@RequestBody bank: Bank): Bank = service.updateBank(bank)

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBank(@PathVariable accountNumber: String):Unit = service.deleteBank(accountNumber)
}

val name = listOf("apple","banana ")