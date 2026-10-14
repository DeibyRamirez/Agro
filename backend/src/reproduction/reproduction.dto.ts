import { IsDateString, IsOptional, IsString } from 'class-validator';

export class CreateReproductionDto {
  @IsString()
  animalId!: string;

  @IsString()
  tipoEvento!: string;

  @IsDateString()
  programada!: string;

  @IsOptional()
  @IsDateString()
  realizada?: string;

  @IsOptional()
  @IsString()
  notas?: string;
}
