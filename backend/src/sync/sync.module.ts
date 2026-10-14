import { Module } from '@nestjs/common';
import { AnimalsModule } from '../animals/animals.module';
import { ProductionModule } from '../production/production.module';
import { TasksModule } from '../tasks/tasks.module';
import { SyncController } from './sync.controller';
import { SyncService } from './sync.service';

@Module({
  imports: [AnimalsModule, ProductionModule, TasksModule],
  controllers: [SyncController],
  providers: [SyncService],
})
export class SyncModule {}
