import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { PrismaService } from '../src/prisma/prisma.service';

describe('AgroControl API (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;

  const prismaMock = {
    $connect: jest.fn(),
    $disconnect: jest.fn(),
    user: {
      findUnique: jest.fn(),
      create: jest.fn(),
    },
    finca: { create: jest.fn() },
    animal: {
      findMany: jest.fn().mockResolvedValue([]),
      findFirst: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      count: jest.fn().mockResolvedValue(0),
    },
    potrero: { count: jest.fn().mockResolvedValue(0), findMany: jest.fn().mockResolvedValue([]) },
    reproductionEvent: { count: jest.fn().mockResolvedValue(0) },
    productionRecord: {
      aggregate: jest.fn().mockResolvedValue({ _sum: { cantidad: 0 } }),
      findMany: jest.fn().mockResolvedValue([]),
    },
    pendingTask: { findMany: jest.fn().mockResolvedValue([]) },
  };

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(PrismaService)
      .useValue(prismaMock)
      .compile();

    app = moduleRef.createNestApplication();
    app.setGlobalPrefix('api/v1');
    app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));
    await app.init();
  });

  afterAll(async () => {
    await app.close();
  });

  it('GET /animals without token returns 401', () => {
    return request(app.getHttpServer()).get('/api/v1/animals').expect(401);
  });

  it('POST /auth/login validates body', () => {
    return request(app.getHttpServer()).post('/api/v1/auth/login').send({}).expect(400);
  });
});
