import { PrismaClient, ProductType, TaskStatus, TaskType } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  const finca = await prisma.finca.upsert({
    where: { id: 'seed-finca-1' },
    update: {},
    create: {
      id: 'seed-finca-1',
      nombre: 'Finca La Esperanza',
    },
  });

  const passwordHash = await bcrypt.hash('demo1234', 10);
  await prisma.user.upsert({
    where: { email: 'demo@agrocontrol.local' },
    update: {},
    create: {
      email: 'demo@agrocontrol.local',
      passwordHash,
      nombre: 'Ganadero Demo',
      fincaId: finca.id,
    },
  });

  const potreros = await Promise.all(
    ['Potrero 1', 'Potrero 2', 'Potrero 3'].map((nombre, i) =>
      prisma.potrero.upsert({
        where: { fincaId_nombre: { fincaId: finca.id, nombre } },
        update: {},
        create: {
          fincaId: finca.id,
          nombre,
          areaHectareas: 2 + i,
          especiesPermitidas: ['Vaca', 'Toro'],
        },
      }),
    ),
  );

  const animals = await Promise.all(
    [
      { codigo: '001', tipo: 'Vaca', raza: 'Holstein', sexo: 'Hembra', peso: 480, potrero: 'Potrero 1' },
      { codigo: '002', tipo: 'Vaca', raza: 'Jersey', sexo: 'Hembra', peso: 420, potrero: 'Potrero 2' },
      { codigo: '003', tipo: 'Toro', raza: 'Brahman', sexo: 'Macho', peso: 650, potrero: 'Potrero 3' },
    ].map((a) =>
      prisma.animal.upsert({
        where: { fincaId_codigo: { fincaId: finca.id, codigo: a.codigo } },
        update: {},
        create: {
          fincaId: finca.id,
          codigo: a.codigo,
          tipo: a.tipo,
          raza: a.raza,
          sexo: a.sexo,
          peso: a.peso,
          fechaNacimiento: new Date('2020-01-15'),
          potreroNombre: a.potrero,
        },
      }),
    ),
  );

  await prisma.productionRecord.createMany({
    data: [
      {
        fincaId: finca.id,
        animalId: animals[0].id,
        productType: ProductType.LECHE,
        cantidad: 186,
        unidad: 'L',
        fecha: new Date(),
      },
      {
        fincaId: finca.id,
        productType: ProductType.CARNE,
        cantidad: 120,
        unidad: 'kg',
        fecha: new Date(),
      },
    ],
    skipDuplicates: true,
  });

  await prisma.pendingTask.createMany({
    data: [
      {
        fincaId: finca.id,
        titulo: 'Fumigación pendiente — Potrero 3',
        tipo: TaskType.POTRERO,
        vencimiento: new Date('2026-10-03'),
        referencia: 'Potrero 3',
      },
      {
        fincaId: finca.id,
        titulo: 'Necesita abono — Potrero 2',
        tipo: TaskType.POTRERO,
        vencimiento: new Date('2026-10-05'),
        referencia: 'Potrero 2',
      },
      {
        fincaId: finca.id,
        titulo: 'Inseminación programada — Vaca 024',
        tipo: TaskType.ANIMAL,
        vencimiento: new Date('2026-10-02'),
        referencia: '024',
      },
    ],
    skipDuplicates: true,
  });

  console.log('Seed OK — demo@agrocontrol.local / demo1234');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
