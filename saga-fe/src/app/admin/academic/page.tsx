
"use client";
import { useState } from 'react';
import { useSemesters } from '@/features/academic/api/useAcademic';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Button } from '@/components/ui/button';

export default function AcademicAdminPage() {
  const [page, setPage] = useState(0);
  const { data: semesterData, isLoading: loadingSemesters } = useSemesters(page, 5);

  return (
    <div className="p-8 max-w-6xl mx-auto space-y-8 bg-slate-50 min-h-screen">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Academic Master Data</h1>
        <Button className="bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-lg hover:shadow-indigo-500/30 transition-all">
          + Add New Semester
        </Button>
      </div>

      <Card className="border-0 shadow-xl shadow-slate-200/50 rounded-2xl overflow-hidden bg-white/80 backdrop-blur-md">
        <CardHeader className="bg-white/50 border-b border-slate-100">
          <CardTitle className="text-xl text-slate-700">Semesters</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/50">
              <TableRow>
                <TableHead className="font-semibold text-slate-600">Name</TableHead>
                <TableHead className="font-semibold text-slate-600">Start Date</TableHead>
                <TableHead className="font-semibold text-slate-600">End Date</TableHead>
                <TableHead className="text-right font-semibold text-slate-600">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loadingSemesters ? (
                <TableRow><TableCell colSpan={4} className="text-center py-8 text-slate-500">Loading...</TableCell></TableRow>
              ) : semesterData?.content.map(sem => (
                <TableRow key={sem.id} className="hover:bg-indigo-50/30 transition-colors">
                  <TableCell className="font-medium text-slate-700">{sem.name}</TableCell>
                  <TableCell className="text-slate-500">{sem.startDate}</TableCell>
                  <TableCell className="text-slate-500">{sem.endDate}</TableCell>
                  <TableCell className="text-right">
                    <Button variant="ghost" size="sm" className="text-indigo-600 hover:text-indigo-700 hover:bg-indigo-50">Edit</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          <div className="p-4 flex items-center justify-between border-t border-slate-100 bg-white/50">
            <span className="text-sm text-slate-500">
              Page {semesterData ? semesterData.pageable.pageNumber + 1 : 1} of {semesterData?.totalPages || 1}
            </span>
            <div className="space-x-2">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
                className="rounded-lg"
              >
                Previous
              </Button>
              <Button
                variant="outline"
                size="sm"
                disabled={!semesterData || page >= semesterData.totalPages - 1}
                onClick={() => setPage(p => p + 1)}
                className="rounded-lg"
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
