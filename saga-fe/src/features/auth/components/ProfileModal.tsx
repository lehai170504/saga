"use client";
import { useAuth } from "@/features/auth/api/useAuth";
import { useIdentity } from "@/features/identity/api/useIdentity";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

interface ProfileModalProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
}

function formatDate(dateStr: string | null | undefined) {
  if (!dateStr) return null;
  const d = new Date(dateStr);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())} ${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
}

export function ProfileModal({ isOpen, onOpenChange }: ProfileModalProps) {
  const { user } = useAuth();
  const { identities, isLoading: isIdentityLoading, unlinkIdentity, isUnlinking } = useIdentity();

  if (!user) return null;

  const handleUnlink = async (provider: "GITHUB" | "JIRA") => {
    try {
      await unlinkIdentity(provider);
      toast.success(`Đã ngắt kết nối với ${provider}`);
    } catch {
      toast.error("Lỗi khi ngắt kết nối");
    }
  };

  const handleLinkGithub = () => {
    const clientId = process.env.NEXT_PUBLIC_GITHUB_CLIENT_ID;
    const redirectUri = encodeURIComponent("http://localhost:3000/identities/callback");
    window.location.href = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=user:email&state=github`;
  };

  const handleLinkJira = () => {
    const clientId = process.env.NEXT_PUBLIC_JIRA_CLIENT_ID;
    const redirectUri = encodeURIComponent("http://localhost:3000/identities/callback");
    window.location.href = `https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=${clientId}&scope=read:me&redirect_uri=${redirectUri}&state=jira&response_type=code&prompt=consent`;
  };

  const githubIdentity = identities?.find(id => id.externalProvider === "GITHUB");
  const jiraIdentity = identities?.find(id => id.externalProvider === "JIRA");

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-gray-800">Hồ sơ & Cài đặt</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-6 py-2">
          <div className="flex items-center gap-5 pb-5 border-b border-gray-100">
            {user.picture ? (
              <img src={user.picture} alt="Avatar" className="w-20 h-20 rounded-full shadow-md object-cover ring-2 ring-blue-100" />
            ) : (
              <div className="w-20 h-20 rounded-full bg-blue-100 flex items-center justify-center text-blue-500 text-3xl font-bold">
                {user.name?.charAt(0) || user.email?.charAt(0) || "U"}
              </div>
            )}
            <div>
              <div className="flex items-center gap-2 mb-1">
                <span className="inline-block px-2 py-0.5 bg-green-100 text-green-700 text-xs font-semibold rounded-full">HOẠT ĐỘNG</span>
              </div>
              <h2 className="text-xl font-semibold text-gray-900">{user.name}</h2>
              <p className="text-gray-500 text-sm mt-0.5">{user.email}</p>
              <span className="inline-block mt-2 px-3 py-1 bg-blue-50 text-blue-600 text-xs font-medium rounded-full">
                {user.role || "Sinh viên"}
              </span>
            </div>
          </div>

          <div>
            <h3 className="text-base font-semibold text-gray-800 mb-1">Tích hợp Hệ thống</h3>
            <p className="text-sm text-gray-400 mb-4">Kết nối tài khoản của bạn với Jira và GitHub để hệ thống tự động ghi nhận khối lượng công việc.</p>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

              <div className={`relative border rounded-xl p-5 flex flex-col gap-3 ${jiraIdentity ? "border-blue-200 bg-blue-50/40" : "border-gray-200 bg-white"}`}>
                {jiraIdentity && (
                  <span className="absolute top-3 right-3 flex items-center gap-1 px-2 py-0.5 bg-green-100 text-green-700 text-xs font-semibold rounded-full">
                    <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"/></svg>
                    Đã kết nối
                  </span>
                )}
                <div className="flex items-center gap-3">
                  <img src="https://cdn.worldvectorlogo.com/logos/jira-3.svg" alt="Jira" className="w-10 h-10" />
                  <div>
                    <p className="font-semibold text-gray-900 text-base">Jira Software</p>
                    <p className="text-xs text-gray-400">Kết nối tài khoản Jira để đồng bộ task.</p>
                  </div>
                </div>
                {jiraIdentity ? (
                  <>
                    <div className="border-t border-blue-100 pt-3 mt-1">
                      <p className="text-xs text-gray-400 font-medium mb-1">Tài khoản kết nối</p>
                      <p className="text-sm font-semibold text-gray-800">
                        {jiraIdentity.name || "Không rõ tên"}
                        {jiraIdentity.email ? ` (${jiraIdentity.email})` : ""}
                      </p>
                      {jiraIdentity.connectedAt && (
                        <p className="text-xs text-gray-400 mt-1">Ngày xác thực: {formatDate(jiraIdentity.connectedAt)}</p>
                      )}
                    </div>
                    <Button variant="destructive" size="sm" onClick={() => handleUnlink("JIRA")} disabled={isUnlinking} className="w-full mt-1">
                      Ngắt kết nối
                    </Button>
                  </>
                ) : (
                  <Button variant="default" size="sm" onClick={handleLinkJira} disabled={isIdentityLoading} className="w-full mt-1 bg-[#0052CC] hover:bg-[#0065FF]">
                    + Liên kết với Jira
                  </Button>
                )}
              </div>

              <div className={`relative border rounded-xl p-5 flex flex-col gap-3 ${githubIdentity ? "border-gray-300 bg-gray-50/60" : "border-gray-200 bg-white"}`}>
                {githubIdentity ? (
                  <span className="absolute top-3 right-3 flex items-center gap-1 px-2 py-0.5 bg-green-100 text-green-700 text-xs font-semibold rounded-full">
                    <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"/></svg>
                    Đã kết nối
                  </span>
                ) : (
                  <span className="absolute top-3 right-3 px-2 py-0.5 bg-gray-100 text-gray-500 text-xs font-semibold rounded-full">Chưa kết nối</span>
                )}
                <div className="flex items-center gap-3">
                  <svg className="w-10 h-10 text-gray-800" viewBox="0 0 24 24" fill="currentColor">
                    <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.462-1.11-1.462-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.161 22 16.416 22 12c0-5.523-4.477-10-10-10z" />
                  </svg>
                  <div>
                    <p className="font-semibold text-gray-900 text-base">GitHub</p>
                    <p className="text-xs text-gray-400">Kết nối tài khoản GitHub để đồng bộ commit.</p>
                  </div>
                </div>
                {githubIdentity ? (
                  <>
                    <div className="border-t border-gray-200 pt-3 mt-1">
                      <p className="text-xs text-gray-400 font-medium mb-1">Tài khoản kết nối</p>
                      <p className="text-sm font-semibold text-gray-800">
                        {githubIdentity.name || "Không rõ tên"}
                        {githubIdentity.email ? ` (${githubIdentity.email})` : ""}
                      </p>
                      {githubIdentity.connectedAt && (
                        <p className="text-xs text-gray-400 mt-1">Ngày xác thực: {formatDate(githubIdentity.connectedAt)}</p>
                      )}
                    </div>
                    <Button variant="destructive" size="sm" onClick={() => handleUnlink("GITHUB")} disabled={isUnlinking} className="w-full mt-1">
                      Ngắt kết nối
                    </Button>
                  </>
                ) : (
                  <Button variant="outline" size="sm" onClick={handleLinkGithub} disabled={isIdentityLoading} className="w-full mt-1 border-gray-800 text-gray-800 hover:bg-gray-800 hover:text-white">
                    + Liên kết với GitHub
                  </Button>
                )}
              </div>

            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
