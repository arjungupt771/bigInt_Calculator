// package com.example.calculator;

// import org.springframework.web.bind.annotation.*;

// import java.math.BigInteger;
// import java.util.*;

// @RestController
// @CrossOrigin(origins = "http://localhost:5173") // React port
// public class CalculatorController {

//     @PostMapping("/calculate")
//     public Map<String, String> calculate(@RequestBody CalcRequest req) {

//         BigInt a = new BigInteger(req.num1);
//         BigInt b = new BigInteger(req.num2);

//         BigInt result;

//         switch (req.operation) {
//             case "add":
//                 result = a.add(b);
//                 break;
//             case "subtract":
//                 result = a.subtract(b);
//                 break;
//             case "multiply":
//                 result = a.karatsubaMultiply(b);
//                 break;
//             case "divide":
//                 result = a.divide(b);
//                 break;
//             case "modulo":
//                 result = a.mod(b);
//                 break;
//             default:
//                 throw new RuntimeException("Invalid operation");
//         }

//         return Map.of("result", result.toString());
//     }
// }
