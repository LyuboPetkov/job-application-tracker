import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <div className="flex items-center gap-6">
        <Link to="/applications" className="text-lg font-bold text-blue-600">
          CAMS
        </Link>
        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            `text-sm font-medium transition-colors ${
              isActive ? 'text-blue-600' : 'text-gray-500 hover:text-gray-900'
            }`
          }
        >
          Dashboard
        </NavLink>
        <NavLink
          to="/applications"
          className={({ isActive }) =>
            `text-sm font-medium transition-colors ${
              isActive ? 'text-blue-600' : 'text-gray-500 hover:text-gray-900'
            }`
          }
        >
          Applications
        </NavLink>
      </div>

      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-600">
          {user?.fullName}
        </span>
        <button
          style={{ cursor: 'pointer' }}
          onClick={handleLogout}
          className="text-sm text-gray-500 hover:text-red-500 transition-colors cursor-pointer"
        >
          Logout
        </button>
      </div>
    </nav>
  )
}

export default Navbar