<?php

namespace App\Domain\Model\Error;

use Core\Domain\Error\BusinessError;
use Fp\Functional\Either\Either;

enum FleetMemberInvitationCode
{
    case FleetNotExists;
    case UserAlreadyExists;
    case UserAlreadyInvited;
}

/**
 * @extends BusinessError<FleetMemberInvitationCode>
 */
readonly class FleetMemberInvitationError extends BusinessError
{
    public function __construct(FleetMemberInvitationCode $message)
    {
        parent::__construct($message->name);
    }

    public static function fleetNotExists(): Either
    {
        return Either::left(new self(FleetMemberInvitationCode::FleetNotExists));
    }

    public static function userAlreadyExists(): Either
    {
        return Either::left(new self(FleetMemberInvitationCode::UserAlreadyExists));
    }

    public static function userAlreadyInvited(): Either
    {
        return Either::left(new self(FleetMemberInvitationCode::UserAlreadyInvited));
    }
}