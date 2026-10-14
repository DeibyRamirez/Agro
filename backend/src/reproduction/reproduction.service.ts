import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateReproductionDto } from './reproduction.dto';

@Injectable()
export class ReproductionService {
  constructor(private readonly prisma: PrismaService) {}

  findAll(fincaId: string) {
    return this.prisma.reproductionEvent.findMany({
      where: { fincaId },
      orderBy: { programada: 'asc' },
      include: { animal: true },
    });
  }

  async findOne(fincaId: string, id: string) {
    const e = await this.prisma.reproductionEvent.findFirst({ where: { id, fincaId } });
    if (!e) throw new NotFoundException('Evento no encontrado');
    return e;
  }

  create(fincaId: string, dto: CreateReproductionDto) {
    return this.prisma.reproductionEvent.create({
      data: {
        fincaId,
        animalId: dto.animalId,
        tipoEvento: dto.tipoEvento,
        programada: new Date(dto.programada),
        realizada: dto.realizada ? new Date(dto.realizada) : null,
        notas: dto.notas ?? '',
      },
    });
  }

  async update(fincaId: string, id: string, dto: Partial<CreateReproductionDto>) {
    await this.findOne(fincaId, id);
    return this.prisma.reproductionEvent.update({
      where: { id },
      data: {
        tipoEvento: dto.tipoEvento,
        programada: dto.programada ? new Date(dto.programada) : undefined,
        realizada: dto.realizada ? new Date(dto.realizada) : undefined,
        notas: dto.notas,
        syncVersion: { increment: 1 },
      },
    });
  }

  async remove(fincaId: string, id: string) {
    await this.findOne(fincaId, id);
    return this.prisma.reproductionEvent.delete({ where: { id } });
  }
}
