<?php

namespace App\Domain\Model;

use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\Fleet\FleetName;
use DateTimeImmutable;

final readonly class Fleet
{
    public function __construct(
        public FleetId           $fleetId,
        public FleetName         $fleetName,
        public DateTimeImmutable $createdAt,
        public DateTimeImmutable $updatedAt,
    ) {}
}
