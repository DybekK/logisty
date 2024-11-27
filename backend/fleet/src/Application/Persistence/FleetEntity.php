<?php

namespace App\Application\Persistence;

use App\Adapter\Outcoming\Repository\PostgresFleetRepository;
use App\Domain\Model\Fleet;
use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\Fleet\FleetName;
use Core\Persistence\EntityBehavior;
use DateTimeImmutable;
use Doctrine\ORM\Mapping as ORM;

/**
 * @implements EntityBehavior<Fleet>
 */
#[ORM\Entity(repositoryClass: PostgresFleetRepository::class)]
#[ORM\Table(name: 'fleet')]
class FleetEntity implements EntityBehavior
{
    #[ORM\Id]
    #[ORM\Column(length: 25)]
    public string $fleetId;

    #[ORM\Column(length: 255)]
    public string $fleetName;

    #[ORM\Column]
    public DateTimeImmutable $createdAt;

    #[ORM\Column]
    public DateTimeImmutable $updatedAt;

    public function toModel(): Fleet
    {
        return new Fleet(
            fleetId: FleetId::of($this->fleetId),
            fleetName: FleetName::of($this->fleetName),
            createdAt: $this->createdAt,
            updatedAt: $this->updatedAt,
        );
    }
}
