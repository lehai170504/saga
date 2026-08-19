import { axiosClient } from "@/lib/axios";

export interface IdentityMap {
  id: string;
  internalUserId: string;
  externalProvider: "GITHUB" | "JIRA";
  externalId: string;
  name: string | null;
  email: string | null;
  connectedAt: string | null;
}

export const identityApi = {
  getMyIdentities: () => {
    return axiosClient.get<{ data: IdentityMap[] }>("/identities/me");
  },

  linkGithub: (code: string) => {
    return axiosClient.post("/identities/github/callback", { code });
  },

  linkJira: (code: string) => {
    return axiosClient.post("/identities/jira/callback", { code });
  },
  unlinkIdentity: (provider: "GITHUB" | "JIRA") => {
    return axiosClient.delete(`/identities/${provider}`);
  }
};
