import { TaskType } from '@prisma/client';
import { IsDateString, IsEnum, IsOptional, IsString } from 'class-validator';

export class CreateTaskDto {
  @IsString()
  titulo!: string;

  @IsEnum(TaskType)
  tipo!: TaskType;

  @IsDateString()
  vencimiento!: string;

  @IsOptional()
  @IsString()
  referencia?: string;
}
