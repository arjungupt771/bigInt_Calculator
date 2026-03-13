// // //package main.java.com.bigint.calculator.controller;
// // import com.bigint.calculator.core.BigInt;
// // import org.springframework.web.bind.annotation.*;

// // import java.util.HashMap;
// // import java.util.Map;

// // @RestController
// // @RequestMapping("/calculate")
// // @CrossOrigin(origins = "*")
// // public class CalculatorController {

// //     @PostMapping
// //     public Map<String, String> calculate(@RequestBody Map<String, String> request) {

// //         Map<String, String> response = new HashMap<>();

// //         try {
// //             // 1️⃣ Read request values
// //             String num1 = request.get("num1");
// //             String num2 = request.get("num2");
// //             String operation = request.get("operation");

// //             if (num1 == null || num2 == null || operation == null) {
// //                 response.put("error", "Invalid request body");
// //                 return response;
// //             }

// //             // 2️⃣ Create BigInt objects using YOUR constructor
// //             BigInt a = new BigInteger(num1);
// //             BigInt b = new BigInteger(num2);
// //             BigInt result;

// //             // 3️⃣ Perform operation using YOUR logic
// //             switch (operation) {

// //                 case "add":
// //                     result = a.add(b);
// //                     break;

// //                 case "subtract":
// //                     result = a.subtract(b);
// //                     break;

// //                 case "multiply":
// //                     result = a.multiply(b);
// //                     break;

// //                 case "karatsuba":
// //                     result = a.karatsubaMultiply(b);
// //                     break;

// //                 case "divide":
// //                     result = a.divide(b);
// //                     break;

// //                 case "modulo":
// //                     result = a.mod(b);
// //                     break;

// //                 default:
// //                     response.put("error", "Unsupported operation");
// //                     return response;
// //             }

// //             // 4️⃣ Return result as string
// //             response.put("result", result.toString());
// //             return response;

// //         } catch (ArithmeticException e) {
// //             // Division / modulo by zero
// //             response.put("error", e.getMessage());
// //             return response;

// //         } catch (Exception e) {
// //             // Invalid number format or unexpected error
// //             response.put("error", "Invalid input");
// //             return response;
// //         }
// //     }
// // }


 package com.bigint.calculator.controller;
import com.bigint.calculator.core.BigInt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// @RestController
// @RequestMapping("/calculate")
// @CrossOrigin(origins = "*")
// public class CalculatorController {

//     @PostMapping
//     public Map<String, String> calculate(@RequestBody Map<String, String> request) {

//         Map<String, String> response = new HashMap<>();

//         try {
//             String num1 = request.get("num1");
//             String num2 = request.get("num2");
//             String operation = request.get("operation");

//             if (num1 == null || num2 == null || operation == null) {
//                 response.put("error", "Invalid request body");
//                 return response;
//             }

//             BigInt a = new BigInt(num1);
//             BigInt b = new BigInt(num2);
//             BigInt result;

//             switch (operation) {
//                 case "add":
//                     result = a.add(b);
//                     break;
//                 case "subtract":
//                     result = a.subtract(b);
//                     break;
//                 case "multiply":
//                     result = a.multiply(b);
//                     break;
//                 case "karatsuba":
//                     result = a.karatsubaMultiply(b);
//                     break;
//                 case "divide":
//                     result = a.divide(b);
//                     break;
//                 case "modulo":
//                     result = a.mod(b);
//                     break;
//                 default:
//                     response.put("error", "Unsupported operation");
//                     return response;
//             }

//             response.put("result", result.toString());
//             return response;

//         } catch (ArithmeticException e) {
//             response.put("error", e.getMessage());
//             return response;
//         } catch (Exception e) {
//             response.put("error", "Invalid input");
//             return response;
//         }
//     }
// }


@RestController
@RequestMapping("/api/calculate")
@CrossOrigin(origins = "http://localhost:5173")
public class CalculatorController {

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Map<String, String> calculate(@RequestBody Map<String, String> request) {

        Map<String, String> response = new HashMap<>();

        try {
            String num1 = request.get("num1");
            String num2 = request.get("num2");
            String operation = request.get("operation");

            if (num1 == null || num2 == null || operation == null) {
                response.put("error", "Invalid request body");
                return response;
            }

            BigInt a = new BigInt(num1);
            BigInt b = new BigInt(num2);
            BigInt result;

            switch (operation) {
                case "add":
                    result = a.add(b);
                    break;
                case "subtract":
                    result = a.subtract(b);
                    break;
                case "multiply":
                    result = a.multiply(b);
                    break;
                case "karatsuba":
                    result = a.karatsubaMultiply(b);
                    break;
                case "divide":
                    result = a.divide(b);
                    break;
                case "modulo":
                    result = a.mod(b);
                    break;
                default:
                    response.put("error", "Unsupported operation");
                    return response;
            }

            response.put("result", result.toString());
            return response;

        } catch (ArithmeticException e) {
            response.put("error", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("error", "Invalid input");
            return response;
        }
    }
}
