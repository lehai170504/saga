import { render, screen } from '@testing-library/react';
import { Header } from '../Header';

// Mock next/link
jest.mock('next/link', () => {
  return ({ children, href }: { children: React.ReactNode; href: string }) => {
    return <a href={href}>{children}</a>;
  };
});

// Mock useAuth
jest.mock('@/features/auth/api/useAuth', () => ({
  useAuth: jest.fn(() => ({
    user: null,
    isUserLoading: false,
    logout: jest.fn(),
  })),
}));

describe('Header Component', () => {
  it('renders the SAGA logo', () => {
    render(<Header />);
    expect(screen.getByText('SAGA')).toBeInTheDocument();
  });

  it('renders navigation links', () => {
    render(<Header />);
    expect(screen.getByText('Tính Năng')).toBeInTheDocument();
    expect(screen.getByText('Đồ Thị')).toBeInTheDocument();
  });

  it('renders the login button', () => {
    render(<Header />);
    const loginLink = screen.getByRole('link', { name: /đăng nhập/i });
    expect(loginLink).toBeInTheDocument();
    expect(loginLink).toHaveAttribute('href', '/');
  });
});
