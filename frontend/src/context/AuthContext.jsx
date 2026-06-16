import {createContext, useContext, useState, useEffect} from 'react'


const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null)
    const [initializing, setInitializing] = useState(true)

useEffect(() => {
  const token = localStorage.getItem('token')
  const email = localStorage.getItem('email')
  const fullName = localStorage.getItem('fullName')
  if (token) {
    setUser({ token, email, fullName })
  }
  setInitializing(false)
}, [])


    function login(data) {
        localStorage.setItem('token', data.token)
        localStorage.setItem('email', data.email)
        localStorage.setItem('fullName', data.fullName)
        setUser({token: data.token, email: data.email, fullName: data.fullName})
    }

    function logout() {
        localStorage.removeItem('token')
        localStorage.removeItem('email')
        localStorage.removeItem('fullName')
        setUser(null)
    }

    return (
        <AuthContext.Provider value={{ user, login, logout, initializing }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext)
}