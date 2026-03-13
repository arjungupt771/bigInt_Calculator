// // API service for Big Integer Calculator
// const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// /**
//  * Sends a calculation request to the backend
//  * @param {string} num1 - First number as string
//  * @param {string} num2 - Second number as string
//  * @param {string} operation - Operation type: 'add', 'subtract', 'multiply', 'divide', 'modulo'
//  * @returns {Promise<{result: string, error?: string}>}
//  */
// export const calculate = async (num1, num2, operation) => {
//   try {
//     const response = await fetch(`${API_BASE_URL}/calculate`, {
//       method: 'POST',
//       headers: {
//         'Content-Type': 'application/json',
//       },
//       body: JSON.stringify({
//         num1,
//         num2,
//         operation,
//       }),
//     });

//     if (!response.ok) {
//       const errorData = await response.json().catch(() => ({}));
//       throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
//     }

//     const data = await response.json();
//     return { result: data.result };
//   } catch (error) {
//     // For demo purposes, if backend is not available, use BigInt locally
//     if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
//       return calculateLocally(num1, num2, operation);
//     }
//     return { error: error.message || 'An unexpected error occurred' };
//   }
// };

// /**
//  * Local fallback calculation using JavaScript BigInt
//  * @param {string} num1 - First number as string
//  * @param {string} num2 - Second number as string
//  * @param {string} operation - Operation type
//  * @returns {{result: string, error?: string}}
//  */
// const calculateLocally = (num1, num2, operation) => {
//   try {
//     const a = BigInt(num1);
//     const b = BigInt(num2);
//     let result;

//     switch (operation) {
//       case 'add':
//         result = a + b;
//         break;
//       case 'subtract':
//         result = a - b;
//         break;
//       case 'multiply':
//         result = a * b;
//         break;
//       case 'divide':
//         if (b === 0n) {
//           return { error: 'Division by zero is not allowed' };
//         }
//         result = a / b;
//         break;
//       case 'modulo':
//         if (b === 0n) {
//           return { error: 'Modulo by zero is not allowed' };
//         }
//         result = a % b;
//         break;
//       default:
//         return { error: 'Invalid operation' };
//     }

//     return { result: result.toString() };
//   } catch (error) {
//     return { error: 'Invalid number format' };
//   }
// };

// export default { calculate };


const API_BASE_URL = "http://localhost:8081";

export type Operation =
  | "add"
  | "subtract"
  | "multiply"
  | "divide"
  | "modulo"
  | "karatsuba";

export interface CalculateRequest {
  num1: string;
  num2: string;
  operation: Operation;
}

export interface CalculateResponse {
  result?: string;
  error?: string;
}

export async function calculate(
  payload: CalculateRequest
): Promise<CalculateResponse> {
  const response = await fetch(`${API_BASE_URL}/api/calculate`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  return response.json();
}

