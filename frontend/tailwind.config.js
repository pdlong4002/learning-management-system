/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class', // Enable class-based dark mode
  theme: {
    extend: {
      colors: {
        leetgray: {
          900: '#1A1A1A', // Main background
          800: '#282828', // Panel background
          700: '#3E3E3E', // Hover state
        },
        leetaccent: '#FFA116', // Primary accent (Leetcode yellow)
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
