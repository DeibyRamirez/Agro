import { IsArray, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreatePotreroDto {
  @IsString()
  nombre!: string;

  @IsOptional()
  @IsNumber()
  areaHectareas?: number;

  @IsOptional()
  @IsString()
  estado?: string;

  @IsOptional()
  @IsArray()
  especiesPermitidas?: string[];
}

export class UpdateSpeciesDto {
  @IsArray()
  especiesPermitidas!: string[];
}
