<?php

namespace App\Adapter\Outcoming\Repository;

use App\Application\Persistence\FleetEntity;
use App\Domain\Model\Fleet;
use App\Domain\Port\FleetRepository;
use Core\Domain\Value\Fleet\FleetId;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<FleetEntity>
 */
class PostgresFleetRepository extends ServiceEntityRepository implements FleetRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, FleetEntity::class);
    }

    public function findById(FleetId $fleetId): ?Fleet
    {
        return $this
            ->find($fleetId->value)
            ?->toModel();
    }
}
