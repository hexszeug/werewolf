import Players, { PlayerListType } from '../players/Players';

export const JudgingView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};
