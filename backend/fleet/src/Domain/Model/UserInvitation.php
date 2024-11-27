<?php

namespace App\Domain\Model;

use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\User\UserEmail;
use Core\Domain\Value\User\UserInvitationId;
use DateTimeImmutable;

final readonly class UserInvitation
{
    public function __construct(
        public UserInvitationId   $invitationId,
        public FleetId            $fleetId,
        public UserEmail          $email,
        public DateTimeImmutable  $dueAt,
        public DateTimeImmutable  $createdAt,
        public ?DateTimeImmutable $acceptedAt = null,
    ) {}
}
