<?php

namespace App\Adapter\Outcoming\Http\Dto;

use App\Domain\Model\UserInvitation;
use Core\Adapter\DtoBehavior;
use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\User\UserEmail;
use Core\Domain\Value\User\UserInvitationId;
use DateTimeImmutable;

/**
 * @implements DtoBehavior<UserInvitation>
 */
final readonly class UserInvitationDto implements DtoBehavior
{
    public function __construct(
        public string             $invitationId,
        public string             $fleetId,
        public string             $email,
        public DateTimeImmutable  $dueAt,
        public DateTimeImmutable  $createdAt,
        public ?DateTimeImmutable $acceptedAt,
    ) {}

    public function toModel(): UserInvitation
    {
        return new UserInvitation(
            invitationId: UserInvitationId::of($this->invitationId),
            fleetId: FleetId::of($this->fleetId),
            email: UserEmail::of($this->email),
            dueAt: $this->dueAt,
            createdAt: $this->createdAt,
            acceptedAt: $this->acceptedAt
        );
    }
}
