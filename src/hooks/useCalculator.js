import { useState, useCallback, useEffect } from 'react';
import { calculate } from '../services/api';

/**
 * Custom hook for calculator state and operations
 * @returns {Object} Calculator state and methods
 */
export const useCalculator = () => {
  const [num1, setNum1] = useState('');
  const [num2, setNum2] = useState('');
  const [result, setResult] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [lastOperation, setLastOperation] = useState(null);
  const [copied, setCopied] = useState(false);

  // Validate input - only allow numeric characters and optional leading minus
  const validateInput = useCallback((value) => {
    // Allow empty string
    if (value === '') return '';
    
    // Allow negative sign at the start
    const isNegative = value.startsWith('-');
    const numericPart = isNegative ? value.slice(1) : value;
    
    // Remove any non-numeric characters
    const cleanedNumeric = numericPart.replace(/[^0-9]/g, '');
    
    // Remove leading zeros (but keep at least one zero if that's all there is)
    const withoutLeadingZeros = cleanedNumeric.replace(/^0+/, '') || (cleanedNumeric.length > 0 ? '0' : '');
    
    return isNegative && withoutLeadingZeros !== '0' ? `-${withoutLeadingZeros}` : withoutLeadingZeros;
  }, []);

  // Handle input changes
  const handleNum1Change = useCallback((value) => {
    const validated = validateInput(value);
    setNum1(validated);
    setError('');
  }, [validateInput]);

  const handleNum2Change = useCallback((value) => {
    const validated = validateInput(value);
    setNum2(validated);
    setError('');
  }, [validateInput]);

  // Check if inputs are valid for calculation
  const isValidForCalculation = useCallback(() => {
    return num1.length > 0 && num2.length > 0 && num1 !== '-' && num2 !== '-';
  }, [num1, num2]);

  // Perform calculation
  const performCalculation = useCallback(async (operation) => {
    if (!isValidForCalculation()) {
      setError('Please enter valid numbers in both fields');
      return;
    }

    setIsLoading(true);
    setError('');
    setResult('');
    setLastOperation(operation);

    const response = await calculate({
        num1,
        num2,
        operation});

    setIsLoading(false);

    if (response.error) {
      setError(response.error);
      setResult('');
    } else {
      setResult(response.result);
      setError('');
    }
  }, [num1, num2, isValidForCalculation]);

  // Operation handlers
  const add = useCallback(() => performCalculation('add'), [performCalculation]);
  const subtract = useCallback(() => performCalculation('subtract'), [performCalculation]);
  const multiply = useCallback(() => performCalculation('multiply'), [performCalculation]);
  const divide = useCallback(() => performCalculation('divide'), [performCalculation]);
  const modulo = useCallback(() => performCalculation('modulo'), [performCalculation]);

  // Clear all
  const clear = useCallback(() => {
    setNum1('');
    setNum2('');
    setResult('');
    setError('');
    setLastOperation(null);
    setCopied(false);
  }, []);

  // Copy result to clipboard
  const copyResult = useCallback(async () => {
    if (!result) return;
    
    try {
      await navigator.clipboard.writeText(result);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  }, [result]);

  // Keyboard support - Enter triggers last operation
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Enter' && lastOperation && isValidForCalculation()) {
        performCalculation(lastOperation);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [lastOperation, performCalculation, isValidForCalculation]);

  return {
    // State
    num1,
    num2,
    result,
    error,
    isLoading,
    lastOperation,
    copied,
    
    // Input handlers
    handleNum1Change,
    handleNum2Change,
    
    // Operations
    add,
    subtract,
    multiply,
    divide,
    modulo,
    clear,
    
    // Utilities
    copyResult,
    isValidForCalculation,
    
    // Digit counts
    num1Digits: num1.replace('-', '').length || 0,
    num2Digits: num2.replace('-', '').length || 0,
    resultDigits: result.replace('-', '').length || 0,
  };
};

export default useCalculator;
