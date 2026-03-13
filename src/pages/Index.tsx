import Calculator from '../components/Calculator';

const Index = () => {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center p-4 sm:p-6 lg:p-8">
      {/* Background decorative elements */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-1/4 -left-32 w-64 h-64 bg-primary/5 rounded-full blur-3xl" />
        <div className="absolute bottom-1/4 -right-32 w-96 h-96 bg-primary/5 rounded-full blur-3xl" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-primary/3 rounded-full blur-3xl opacity-30" />
      </div>

      {/* Main content */}
      <div className="relative z-10 w-full max-w-2xl">
        <Calculator />
      </div>

      {/* Footer */}
      <footer className="relative z-10 mt-8 text-center">
        <p className="text-xs text-muted-foreground/50">
          Built for high-precision integer calculations
        </p>
      </footer>
    </main>
  );
};

export default Index;
