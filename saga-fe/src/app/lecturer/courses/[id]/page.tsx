
"use client";


import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useImportRoster } from '@/features/academic/api/useLecturerAcademic';
import { toast } from 'sonner';

export default function LecturerCoursePage() {
  const params = useParams();
  const courseId = params.id as string;
  const { mutate: importRoster, isPending } = useImportRoster();

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      if (file.size > 5 * 1024 * 1024) {
        toast.error("File size exceeds 5MB limit");
        return;
      }
      importRoster({ courseId, file }, {
        onSuccess: () => toast.success("Roster imported successfully!"),
        onError: (err: any) => toast.error(err.response?.data?.message || "Failed to import roster")
      });
    }
  };

  return (
    <div className="p-8 max-w-6xl mx-auto space-y-8 bg-slate-50 min-h-screen">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Course Management</h1>
          <p className="text-slate-500 mt-1">Manage your course roster and student teams.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <Card className="col-span-1 border-0 shadow-xl shadow-indigo-100 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 text-white">
          <CardHeader>
            <CardTitle className="text-xl text-white">Import Roster</CardTitle>
            <CardDescription className="text-indigo-100">Upload an Excel file to import students and assign teams.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="border-2 border-dashed border-indigo-300/50 rounded-xl p-8 text-center bg-white/10 hover:bg-white/20 transition-colors relative">
              <input
                type="file"
                accept=".xlsx"
                onChange={handleFileUpload}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                disabled={isPending}
              />
              <div className="space-y-2">
                <svg className="mx-auto h-8 w-8 text-indigo-200" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <p className="text-sm font-medium">{isPending ? "Uploading..." : "Click or drag file here"}</p>
                <p className="text-xs text-indigo-200">Max size: 5MB (.xlsx only)</p>
              </div>
            </div>
            <Button variant="secondary" className="w-full bg-white text-indigo-600 hover:bg-indigo-50" onClick={() => window.location.href = `/api/v1/academic/courses/${courseId}/roster/template`}>
              Download Template
            </Button>
          </CardContent>
        </Card>

        <Card className="col-span-2 border-0 shadow-xl shadow-slate-200/50 rounded-2xl bg-white/80 backdrop-blur-md">
          <CardHeader className="bg-white/50 border-b border-slate-100">
            <CardTitle className="text-xl text-slate-700">Team Groupings</CardTitle>
          </CardHeader>
          <CardContent className="p-8 text-center text-slate-500">
            <p>Upload a roster to generate teams.</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
