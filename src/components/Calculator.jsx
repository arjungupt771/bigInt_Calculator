import React from 'react';
import InputField from './InputField';
import OperationButtons from './OperationButtons';
import ResultBox from './ResultBox';
import { useCalculator } from '../hooks/useCalculator';

/**
 * Main Calculator component
 * Big Integer Calculator that handles extremely large numbers
 */
const Calculator = () => {
  const {
    num1,
    num2,
    result,
    error,
    isLoading,
    lastOperation,
    copied,
    handleNum1Change,
    handleNum2Change,
    add,
    subtract,
    multiply,
    divide,
    modulo,
    clear,
    copyResult,
    isValidForCalculation,
    num1Digits,
    num2Digits,
    resultDigits,
  } = useCalculator();

  return (
    <div className="calculator-card w-full max-w-2xl mx-auto rounded-2xl p-6 sm:p-8 animate-scale-in">
      {/* Header */}
      <div className="text-center mb-8">
        <div className="flex items-center justify-center gap-3 mb-2">
          <div className="w-10 h-10 rounded-xl bg-primary/20 flex items-center justify-center">
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              width="24" 
              height="24" 
              viewBox="0 0 24 24" 
              fill="none" 
              stroke="currentColor" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
              className="text-primary"
            >
              <rect x="4" y="2" width="16" height="20" rx="2"></rect>
              <line x1="8" y1="6" x2="16" y2="6"></line>
              <line x1="16" y1="14" x2="16" y2="18"></line>
              <path d="M16 10h.01"></path>
              <path d="M12 10h.01"></path>
              <path d="M8 10h.01"></path>
              <path d="M12 14h.01"></path>
              <path d="M8 14h.01"></path>
              <path d="M12 18h.01"></path>
              <path d="M8 18h.01"></path>
            </svg>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold text-foreground">
            Big Integer Calculator
          </h1>
        </div>
        <p className="text-muted-foreground text-sm">
          Perform precise calculations on numbers with 100+ digits
        </p>
      </div>

      {/* Input Fields */}
      <div className="space-y-4 mb-6">
        <InputField
          value={num1}
          onChange={handleNum1Change}
          label="First Number"
          placeholder="Enter first number..."
          digitCount={num1Digits}
          disabled={isLoading}
        />
        <InputField
          value={num2}
          onChange={handleNum2Change}
          label="Second Number"
          placeholder="Enter second number..."
          digitCount={num2Digits}
          disabled={isLoading}
        />
      </div>

      {/* Operation Buttons */}
      <div className="mb-6">
        <OperationButtons
          onAdd={add}
          onSubtract={subtract}
          onMultiply={multiply}
          onDivide={divide}
          onModulo={modulo}
          onClear={clear}
          disabled={!isValidForCalculation()}
          isLoading={isLoading}
          lastOperation={lastOperation}
        />
      </div>

      {/* Result Box */}
      <ResultBox
        result={result}
        error={error}
        isLoading={isLoading}
        onCopy={copyResult}
        copied={copied}
        digitCount={resultDigits}
      />

      {/* Footer hint */}
      <div className="mt-6 text-center">
        <p className="text-xs text-muted-foreground/70">
          Press <kbd className="px-1.5 py-0.5 rounded bg-secondary text-muted-foreground text-[10px] font-mono">Enter</kbd> to repeat the last operation
        </p>
      </div>
    </div>
  );
};

export default Calculator;
