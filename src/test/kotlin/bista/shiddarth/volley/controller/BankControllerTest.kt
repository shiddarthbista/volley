package bista.shiddarth.volley.controller

import bista.shiddarth.volley.model.Bank
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
internal class BankControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {



    val baseURL = "/api/banks"

    @Nested
    @DisplayName("GET api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBanks {
        @Test
        fun `should return all banks`() {
            mockMvc.get(baseURL)
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].accountNumber") { value("SW1234") }
                }
        }
    }

    @Nested
    @DisplayName("GET api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBank {
        @Test
        fun `should return the bank with the given account number`() {
            val accountNumber = "SW1234"

            mockMvc.get("$baseURL/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.trust") { value("2.0") }
                    jsonPath("$.transactionFee") { value("1") }
                }
        }

        @Test
        fun `should return BAD REQUEST if pattern does not match`() {
            val accountNumber = "SW12344"

            mockMvc.get("$baseURL/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `should return NOT FOUND if the account number does not exist`() {
            val accountNumber = "SW9999"

            mockMvc.get("$baseURL/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }

    }

    @Nested
    @DisplayName("POST /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostNewBank {
        @Test
        fun `should add new bank`() {
            val newBank = Bank("SW7865", 2.4, 2)

            mockMvc.post(baseURL) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(newBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }

            mockMvc.get("$baseURL/${newBank.accountNumber}")
                .andExpect { content { json(objectMapper.writeValueAsString(newBank)) } }

        }

        @Test
        fun `should return BAD REQUEST if bank already exists`() {
            val invalidBank = Bank("SW1234", 2.0, 1)

            mockMvc.post(baseURL) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }


    }

    @Nested
    @DisplayName("PATCH api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PatchExistingBank {

        @Test
        fun `should update an existing bank`() {
            val updatedBank = Bank("SW1234", 1.2, 2)

            mockMvc.patch(baseURL) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatedBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        MediaType.APPLICATION_JSON
                        json(objectMapper.writeValueAsString(updatedBank))
                    }
                }

            mockMvc.get("$baseURL/${updatedBank.accountNumber}")
                .andExpect { content { json(objectMapper.writeValueAsString(updatedBank)) } }
        }

        @Test
        fun `should return BAD REQUEST if no bank with given account number exists`() {
            val invalidBank = Bank("does_not_exist", 1.8, 2)

            mockMvc.patch(baseURL) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }
    }

    @Nested
    @DisplayName("DELETE api/banks/(accountNumber}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeleteExistingBank {

        @Test
        fun `should delete the bank with the given account number`() {
            val accountNumber = "SW1234"

            mockMvc.delete("$baseURL/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

            mockMvc.get("$baseURL/$accountNumber")
                .andExpect { status { isNotFound() } }
        }

        @Test
        fun`should return NOT FOUND if no bank with given number exists`(){
            val invalidAccountNumber = "does_not_exist"

            mockMvc.delete("$baseURL/$invalidAccountNumber")
                .andDo { print() }
                .andExpect { status { isNotFound() } }

        }

    }
}
