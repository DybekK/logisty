<?php

namespace Core\Domain\Value\User;

use Core\Domain\Value\ValueObject;
use Core\Persistence\CuidBehavior;
use Visus\Cuid2\Cuid2;

/**
 * @implements ValueObject<string, UserId>
 * @implements CuidBehavior<UserId>
 */
final readonly class UserId implements ValueObject, CuidBehavior
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
        return $object instanceof UserId && $object->value === $this->value;
    }
}
