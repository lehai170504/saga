import { create } from 'zustand';

interface AuthUIState {
  isLoginModalOpen: boolean;
  setLoginModalOpen: (isOpen: boolean) => void;
  toggleLoginModal: () => void;
}

export const useAuthUIStore = create<AuthUIState>((set) => ({
  isLoginModalOpen: false,
  setLoginModalOpen: (isOpen) => set({ isLoginModalOpen: isOpen }),
  toggleLoginModal: () => set((state) => ({ isLoginModalOpen: !state.isLoginModalOpen })),
}));
