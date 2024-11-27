<?php

namespace Core\Domain\Value\User;

use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\ValueObject;
use Core\Persistence\CuidBehavior;
use Visus\Cuid2\Cuid2;

/**
 * @implements ValueObject<string, UserInvitationId>
 * @implements CuidBehavior<UserInvitationId>
 */
final readonly class UserInvitationId implements ValueObject, CuidBehavior
{
    private function __construct(
        public string $value,
    ) {}

    public static function random()
    {
        return new self(new Cuid2());
    }

    public static function of(mixed $value): self
    {
        return new self($value);
    }

    public function eq($object): bool
    {
        return $object instanceof FleetId && $object->value === $this->value;
    }
}
