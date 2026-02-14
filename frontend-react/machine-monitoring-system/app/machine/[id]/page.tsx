import { MachineDetail } from "@/components/machine-detail"

export default async function MachinePage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = await params

  return <MachineDetail machineId={id} />
}
