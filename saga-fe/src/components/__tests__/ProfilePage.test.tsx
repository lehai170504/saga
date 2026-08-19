import { render, screen, fireEvent } from '@testing-library/react';
import ProfilePage from '../../app/profile/page';
import { useAuth } from '@/features/auth/api/useAuth';
import { useIdentity } from '@/features/identity/api/useIdentity';
import { useRouter } from 'next/navigation';

jest.mock('@/features/auth/api/useAuth');
jest.mock('@/features/identity/api/useIdentity');
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

describe('ProfilePage', () => {
  const mockPush = jest.fn();

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    (useAuth as jest.Mock).mockReturnValue({
      user: { name: 'Test User', email: 'test@example.com', role: 'STUDENT', picture: '' },
      isUserLoading: false,
    });
    (useIdentity as jest.Mock).mockReturnValue({
      identities: [],
      isLoading: false,
    });
    jest.clearAllMocks();
  });

  it('redirects to home if user is not logged in', () => {
    (useAuth as jest.Mock).mockReturnValue({ user: null, isUserLoading: false });
    render(<ProfilePage />);
    expect(mockPush).toHaveBeenCalledWith('/');
  });

  it('renders user information correctly', () => {
    render(<ProfilePage />);
    expect(screen.getByText('Hồ sơ cá nhân')).toBeInTheDocument();
    expect(screen.getByText('Test User')).toBeInTheDocument();
  });

  it('shows not linked status for github and jira initially', () => {
    render(<ProfilePage />);
    const linkButtons = screen.getAllByText('Liên kết ngay');
    expect(linkButtons).toHaveLength(2);
  });

  it('shows linked status when identities are present', () => {
    (useIdentity as jest.Mock).mockReturnValue({
      identities: [{ externalProvider: 'GITHUB' }, { externalProvider: 'JIRA' }],
      isLoading: false,
    });
    render(<ProfilePage />);
    const linkedButtons = screen.getAllByText('Đã liên kết');
    expect(linkedButtons.length).toBeGreaterThanOrEqual(2);
  });
});
